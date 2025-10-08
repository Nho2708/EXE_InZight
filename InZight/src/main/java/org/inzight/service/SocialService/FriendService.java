package org.inzight.service.SocialService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.FriendRequest;
import org.inzight.dto.response.FriendResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Friend;
import org.inzight.entity.Post;
import org.inzight.entity.User;
import org.inzight.enums.FriendStatus;
import org.inzight.mapper.FriendMapper;
import org.inzight.repository.FriendRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public List<FriendResponse> getALlFriends() {
        Long currentUserId = authUtil.getCurrentUserId();
        return friendRepository.findByUserIdOrFriendId(currentUserId, currentUserId)
                .stream()
                .map(friendMapper::toResponse)
                .collect(Collectors.toList());
    }
    public void sendFriendRequest(Long receiverId) {
        Long senderId = authUtil.getCurrentUserId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));


        if (friendRepository.existsByUserAndFriend(sender, receiver)) {
            throw new RuntimeException("Friend request already exists");
        }

        Friend friend = Friend.builder()
                .user(sender)
                .friend(receiver)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);

        FriendResponse response = friendMapper.toResponse(friend);


        messagingTemplate.convertAndSend("/topic/friend/" + receiverId, response);
    }
    public void acceptFriendRequest(Long senderId) {
        Long currentUserId = authUtil.getCurrentUserId();


        Friend friendship = friendRepository
                .findByUserIdAndFriendId(senderId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Cannot found friendship."));

        if (friendship.getStatus() != FriendStatus.PENDING) {
            throw new RuntimeException("Accept friend already exists.");
        }

        friendship.setStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendship);


        messagingTemplate.convertAndSend("/topic/friend/" + senderId,
                "User " + currentUserId + " accepted friendship.");
    }
    public void blockFriend(Long friendId) {
        Long currentUserId = authUtil.getCurrentUserId();

        Friend friendship = friendRepository
                .findByUserIdAndFriendIdOrFriendIdAndUserId(currentUserId, friendId, friendId, currentUserId)
                .orElseThrow(() -> new RuntimeException("No friendships found."));
        if (friendship.getStatus() != FriendStatus.ACCEPTED) {
            throw new RuntimeException("Only accepted friends can be blocked.");
        }
        friendship.setStatus(FriendStatus.BLOCKED);
        friendRepository.save(friendship);


        messagingTemplate.convertAndSend("/topic/friend/" + friendId,
                "You be block by user ID " + currentUserId);
    }
    public void unblockFriend(Long friendId) {
        Long currentUserId = authUtil.getCurrentUserId();

        Friend friendship = friendRepository
                .findByUserIdAndFriendIdOrFriendIdAndUserId(currentUserId, friendId, friendId, currentUserId)
                .orElseThrow(() -> new RuntimeException("No friendships found."));

        if (friendship.getStatus() == FriendStatus.BLOCKED) {
            friendship.setStatus(FriendStatus.ACCEPTED);
            friendRepository.save(friendship);

            messagingTemplate.convertAndSend("/topic/friend/" + friendId,
                    "User " + currentUserId + " unblock you.");
        } else {
            throw new RuntimeException("Can not unblock friend.");
        }
    }
    public void removeFriend(Long friendId) {
        Long currentUserId = authUtil.getCurrentUserId();

        Friend friendship = friendRepository
                .findByUserIdAndFriendIdOrFriendIdAndUserId(currentUserId, friendId, friendId, currentUserId)
                .orElseThrow(() -> new RuntimeException("No friendships found."));

        friendRepository.delete(friendship);

        messagingTemplate.convertAndSend("/topic/friend/" + friendId,
                "User " + currentUserId + " unfriended you.");
    }



}
