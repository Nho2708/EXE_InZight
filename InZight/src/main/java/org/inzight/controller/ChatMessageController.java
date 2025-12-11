package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChatMessageRequest;
import org.inzight.dto.response.ChatMessageResponse;
import org.inzight.service.SocialService.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChatMessageController {
    private final ChatMessageService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request) {
        chatService.sendMessage(request);
    }

    @MessageMapping("/chat.ai")
    public void chatWithAi(ChatMessageRequest request) {
        chatService.sendAiMessage(request != null ? request.getContent() : null);
    }
}
