package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.service.SocialService.CommentLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment-likes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long commentId) {
        String result = commentLikeService.toggleLike(commentId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{commentId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentLikeService.countLikes(commentId));
    }
}
