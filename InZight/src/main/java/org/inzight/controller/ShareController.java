package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.LikeResponse;
import org.inzight.dto.response.ShareResponse;
import org.inzight.service.SocialService.ShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor
public class ShareController {
    private final ShareService shareService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> sharePost(@PathVariable Long postId) {
        boolean shared = shareService.toggleShare(postId);
        return shared
                ? ResponseEntity.ok("Post shared")
                : ResponseEntity.ok("Post unshared");
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getShareCount(@PathVariable Long postId) {
        return ResponseEntity.ok(shareService.getShareCount(postId));
    }

    @GetMapping("/{postId}/shares") // get like kem UserName
    public ResponseEntity<List<ShareResponse>> getShares(@PathVariable Long postId) {
        return ResponseEntity.ok(shareService.getShares(postId));
    }
}
