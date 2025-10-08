package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.FriendResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.service.SocialService.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FriendshipController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getAll() {
        return ResponseEntity.ok(friendService.getALlFriends());
    }

    @PostMapping("/invite/{receiverId}")
    public ResponseEntity<String> invite(@PathVariable Long receiverId) {
        friendService.sendFriendRequest(receiverId);
        return ResponseEntity.ok("Sent friend request successfully!");
    }
    @PostMapping("/{senderId}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long senderId) {
        friendService.acceptFriendRequest(senderId);
        return ResponseEntity.ok("Accepted from user " + senderId);
    }
    @PostMapping("/{friendId}/block")
    public ResponseEntity<?> blockFriend(@PathVariable Long friendId) {
        friendService.blockFriend(friendId);
        return ResponseEntity.ok("Blocked!");
    }
    @PostMapping("/{friendId}/unblock")
    public ResponseEntity<String> unblockFriend(@PathVariable Long friendId) {
        friendService.unblockFriend(friendId);
        return ResponseEntity.ok("Đã bỏ chặn người dùng ID " + friendId);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable Long friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.ok("Đã xóa bạn ID " + friendId);
    }
}
