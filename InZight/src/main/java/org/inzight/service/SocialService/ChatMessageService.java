package org.inzight.service.SocialService;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChatMessageRequest;
import org.inzight.dto.response.ChatMessageResponse;
import org.inzight.entity.ChatMessage;
import org.inzight.entity.User;
import org.inzight.repository.ChatMessageRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.inzight.service.Ai.GeminiAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final GeminiAiService geminiAiService;
    @Value("${ai.bot-id:24}")
    private Long finbotUserId;

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        Long senderId = authUtil.getCurrentUserId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        chatMessageRepository.save(message);

        ChatMessageResponse dto = mapToResponse(message);

        // Gửi realtime cho cả receiver và sender để đồng bộ UI
        messagingTemplate.convertAndSend("/topic/chat/" + receiver.getId(), dto);
        messagingTemplate.convertAndSend("/topic/chat/" + sender.getId(), dto);

        return dto;
    }

    public List<ChatMessageResponse> getHistory(Long receiverId) {
        Long currentUserId = authUtil.getCurrentUserId();

        List<ChatMessage> messages = chatMessageRepository.findChatBetween(currentUserId, receiverId);

        return messages.stream()
                .map(m -> ChatMessageResponse.builder()
                        .id(m.getId())
                        .senderId(m.getSender().getId())
                        .senderName(m.getSender().getFullName())
                        .receiverId(m.getReceiver().getId())
                        .receiverName(m.getReceiver().getFullName())
                        .content(m.getContent())
                        .createdAt(
                                LocalDateTime.ofInstant(m.getCreatedAt(), ZoneId.of("Asia/Ho_Chi_Minh"))
                        )
                        .build())
                .toList();
    }

    /**
     * Gửi tin nhắn AI: lưu tin nhắn user, gọi Gemini API, lưu reply, push realtime.
     */
    public ChatMessageResponse sendAiMessage(String content) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            // Lấy user FinBot từ DB
            User finbot = userRepository.findById(finbotUserId)
                    .orElseThrow(() -> new RuntimeException("FinBot user not found, id=" + finbotUserId));
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found, id=" + currentUserId));

            String userText = Optional.ofNullable(content).orElse("").trim();
            
            // Kiểm tra xem đây có phải là tin nhắn đầu tiên trong cuộc trò chuyện không (TRƯỚC KHI lưu)
            List<ChatMessage> existingMessages = chatMessageRepository.findChatBetween(currentUserId, finbotUserId);
            boolean isFirstMessage = existingMessages.stream()
                    .filter(m -> m.getSender().getId().equals(currentUserId))
                    .count() == 0; // Chưa có tin nhắn nào từ user
            
            // Kiểm tra xem tin nhắn có chứa giao dịch không (có số tiền)
            boolean hasTransaction = detectTransaction(userText);
            
            // Lưu tin nhắn của user
            if (!userText.isEmpty()) {
                ChatMessage userMessage = ChatMessage.builder()
                        .sender(currentUser)
                        .receiver(finbot)
                        .content(userText)
                        .createdAt(Instant.now())
                        .build();
                chatMessageRepository.save(userMessage);
                
                // Push user message realtime
                ChatMessageResponse userMsgDto = mapToResponse(userMessage);
                messagingTemplate.convertAndSend("/topic/chat/" + currentUserId, userMsgDto);
            }
            
            // Chỉ trả lời nếu: (1) có giao dịch HOẶC (2) là tin nhắn đầu tiên
            if (!hasTransaction && !isFirstMessage) {
                // Không trả lời, chỉ lưu tin nhắn của user
                return null;
            }
            
            // Lấy lịch sử chat gần đây để AI hiểu context (chỉ khi cần trả lời)
            // Bao gồm cả tin nhắn vừa lưu
            List<ChatMessage> allMessagesNow = chatMessageRepository.findChatBetween(currentUserId, finbotUserId);
            List<ChatMessage> recentMessages = allMessagesNow.stream()
                    .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt())) // Mới nhất trước
                    .limit(10) // Lấy 10 tin nhắn gần nhất
                    .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())) // Sắp xếp lại theo thứ tự thời gian
                    .toList();
            
            // Gọi Gemini AI với context của cuộc trò chuyện
            String reply = geminiAiService.generateReply(userText, recentMessages)
                    .filter(r -> r != null && !r.isBlank())
                    .orElse(null);
            
            // Nếu AI không trả về reply, tạo một reply mặc định "lầy lội" dựa trên context
            if (reply == null || reply.isEmpty()) {
                // Fallback: tạo reply mặc định dựa trên nội dung tin nhắn của user
                reply = generateContextualFallbackReply(userText, hasTransaction, isFirstMessage);
            }
            
            // Lưu và trả về reply
            ChatMessage aiEntity = ChatMessage.builder()
                    .sender(finbot)
                    .receiver(currentUser)
                    .content(reply)
                    .createdAt(Instant.now())
                    .build();

            chatMessageRepository.save(aiEntity);

            ChatMessageResponse ai = mapToResponse(aiEntity);

            // Push realtime
            messagingTemplate.convertAndSend("/topic/chat/" + currentUserId, ai);
            // Trả về cho REST fallback
            return ai;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send AI message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Kiểm tra xem tin nhắn có chứa giao dịch không (có số tiền)
     */
    private boolean detectTransaction(String userText) {
        if (userText == null || userText.isBlank()) {
            return false;
        }
        
        // Pattern để phát hiện số tiền: "40k", "100k", "1tr", "50000", "50.000", v.v.
        String lowerText = userText.toLowerCase();
        
        // Kiểm tra pattern số + k/tr/nghìn/triệu
        if (lowerText.matches(".*\\d+\\s*([ktr]|nghìn|triệu|ngàn)\\b.*")) {
            return true;
        }
        
        // Kiểm tra số lớn (>= 4 chữ số) - có thể là số tiền
        if (lowerText.matches(".*\\b\\d{4,}(?:[.,]\\d{3})*(?:[.,]\\d{2})?\\b.*")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Tạo fallback reply dựa trên context của tin nhắn user và lịch sử chat
     */
    private String generateContextualFallbackReply(String userText, boolean hasTransaction, boolean isFirstMessage) {
        if (userText == null || userText.isBlank()) {
            if (isFirstMessage) {
                return "Chào bạn! 👋 Mình là Finbot đây! Mình có thể giúp bạn ghi lại chi tiêu hoặc trò chuyện cùng bạn đấy! 😊";
            }
            return "Ồ, bạn có nói gì không? Mình không nghe rõ đấy! 😅";
        }
        
        String lowerText = userText.toLowerCase();
        
        // Nếu là tin nhắn đầu tiên, chào hỏi
        if (isFirstMessage) {
            String[] firstMessageReplies = {
                "Chào bạn! 👋 Mình là Finbot đây! Mình có thể giúp bạn ghi lại chi tiêu hoặc trò chuyện cùng bạn đấy! 😊",
                "Xin chào! 😎 Mình là Finbot, trợ lý tài chính của bạn! Bạn có thể nói với mình về chi tiêu như 'trà sữa 40k' hoặc 'ăn sáng 30k' nhé!",
                "Chào bạn! 👋 Mình là Finbot! Bạn chỉ cần nói với mình về giao dịch như 'trà sữa 40k', mình sẽ tự động ghi chép cho bạn! 😄"
            };
            long timestamp = System.currentTimeMillis();
            return firstMessageReplies[(int)(timestamp % firstMessageReplies.length)];
        }
        
        // Nếu có giao dịch, trả lời về giao dịch
        if (hasTransaction) {
            String[] transactionReplies = {
                "Ồ, mình thấy bạn đang nói về giao dịch đấy! 💰 Mình đã ghi chép rồi nhé! 😊",
                "Giao dịch của bạn đã được ghi lại! 💵 Có gì cần mình giúp thêm không?",
                "Mình đã thấy giao dịch của bạn rồi! 💸 Bạn có muốn ghi chép thêm gì nữa không?",
                "Ồ, giao dịch mới à? 💰 Mình đã lưu lại rồi! 😄",
                "Ghi chép xong rồi! 💵 Bạn có giao dịch nào khác không?"
            };
            long timestamp = System.currentTimeMillis();
            return transactionReplies[(int)((Math.abs(userText.hashCode()) + timestamp) % transactionReplies.length)];
        }
        
        // Nếu không phải giao dịch và không phải tin nhắn đầu tiên, không nên đến đây
        // Nhưng để an toàn, trả về một reply mặc định
        return "Mình đã nhận được tin nhắn của bạn! 😊";
    }

    private ChatMessageResponse mapToResponse(ChatMessage m) {
        return ChatMessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getFullName() != null ? m.getSender().getFullName() : m.getSender().getUsername())
                .receiverId(m.getReceiver().getId())
                .receiverName(m.getReceiver().getFullName() != null ? m.getReceiver().getFullName() : m.getReceiver().getUsername())
                .content(m.getContent())
                .createdAt(
                        LocalDateTime.ofInstant(m.getCreatedAt(), ZoneId.of("Asia/Ho_Chi_Minh"))
                )
                .build();
    }
}
