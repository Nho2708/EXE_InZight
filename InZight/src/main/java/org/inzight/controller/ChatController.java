package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChatMessageRequest;
import org.inzight.dto.response.ChatMessageResponse;
import org.inzight.entity.ChatMessage;
import org.inzight.service.SocialService.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChatController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/history/{receiverId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable Long receiverId) {
        List<ChatMessageResponse> history = chatMessageService.getHistory(receiverId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessageRequest request) {
        chatMessageService.sendMessage(request);
        return ResponseEntity.ok().build();
    }
}
