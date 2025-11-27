package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.LikeResponse;
import org.inzight.dto.response.ShareResponse;
import org.inzight.service.SocialService.ShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class ShareController {
    private final ShareService shareService;


    // share or unshare
    @PostMapping("/{postId}")
    public ResponseEntity<String> sharePost(@PathVariable Long postId) {
        boolean shared = shareService.toggleShare(postId);
        return shared
                ? ResponseEntity.ok("Post shared")
                : ResponseEntity.ok("Post unshared");
    }
    // get counts share
    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getShareCount(@PathVariable Long postId) {
        return ResponseEntity.ok(shareService.getShareCount(postId));
    }
    // get all share with username
    @GetMapping("/{postId}/shares")
    public ResponseEntity<List<ShareResponse>> getShares(@PathVariable Long postId) {
        return ResponseEntity.ok(shareService.getShares(postId));
    }
}
