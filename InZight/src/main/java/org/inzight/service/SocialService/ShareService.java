package org.inzight.service.SocialService;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.LikeResponse;
import org.inzight.dto.response.ShareResponse;
import org.inzight.entity.Post;
import org.inzight.entity.Share;
import org.inzight.entity.User;
import org.inzight.repository.PostRepository;
import org.inzight.repository.ShareRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Transactional
    public boolean toggleShare(Long postId) {
        Long currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Share> existing = shareRepository.findByPostIdAndUserId(postId, currentUserId);
        if (existing.isPresent()) {
            shareRepository.delete(existing.get());
            return false; // hủy share
        }

        Share share = Share.builder()
                .post(post)
                .user(user)
                .build();

        shareRepository.save(share);
        return true; // share mới
    }

    public Long getShareCount(Long postId) {
        return shareRepository.countByPostId(postId);
    }

    public List<ShareResponse> getShares(Long postId) {
        return shareRepository.findByPostId(postId)
                .stream()
                .map(share -> ShareResponse.builder()
                        .userId(share.getUser().getId())
                        .username(share.getUser().getUsername())
                        .build())
                .toList();
    }
}
