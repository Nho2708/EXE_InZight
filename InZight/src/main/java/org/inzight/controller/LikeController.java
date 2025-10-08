package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.LikeResponse;
import org.inzight.service.SocialService.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;


    // like or unlike
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId) {
        boolean liked = likeService.toggleLike(postId);
        return liked
                ? ResponseEntity.ok("Post liked")
                : ResponseEntity.ok("Post unliked");
    }

    // get counts like
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikeCount(postId));
    }

    // get all like with username
    @GetMapping("/{postId}/likes") // get like kem UserName
    public ResponseEntity<List<LikeResponse>> getLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikes(postId));
    }
}
