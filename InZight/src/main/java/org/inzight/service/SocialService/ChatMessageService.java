package org.inzight.service.SocialService;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChatMessageRequest;
import org.inzight.entity.ChatMessage;
import org.inzight.entity.User;
import org.inzight.repository.ChatMessageRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public void sendMessage(ChatMessageRequest request) {
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

        // Gửi realtime qua WebSocket cho receiver
        messagingTemplate.convertAndSend("/topic/chat/" + request.getReceiverId(), message);
    }
}
