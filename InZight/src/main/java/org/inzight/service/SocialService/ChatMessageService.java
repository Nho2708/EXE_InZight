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
            
            // Lưu tin nhắn của user trước
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
            
            // Gọi Gemini AI - chỉ trả về reply thực sự từ AI, không có fallback message
            String reply = geminiAiService.generateReply(userText)
                    .filter(r -> !r.isBlank())
                    .orElse(null);
            
            // Chỉ lưu reply nếu có (không có fallback message nữa)
            if (reply != null && !reply.isEmpty()) {
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
            }
            
            // Nếu không có reply từ AI, trả về null (frontend sẽ không hiển thị gì)
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send AI message: " + e.getMessage(), e);
        }
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
