package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ReplyRequest;
import org.inzight.dto.response.ReplyResponse;
import org.inzight.service.SocialService.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ReplyController.java
@RestController
@RequestMapping("/api/replies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<ReplyResponse> addReply(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReplyRequest request) {
        return ResponseEntity.ok(replyService.addReply(userDetails, request));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReplyResponse>> getRepliesByComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(replyService.getRepliesByComment(commentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }
}
