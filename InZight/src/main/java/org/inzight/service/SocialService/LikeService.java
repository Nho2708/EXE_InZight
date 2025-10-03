package org.inzight.service.SocialService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.LikeResponse;
import org.inzight.entity.Like;
import org.inzight.entity.Post;
import org.inzight.entity.User;
import org.inzight.repository.LikeRepository;
import org.inzight.repository.PostRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    @Transactional
    public boolean toggleLike(Long postId) {
        Long currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Optional<Like> existing = likeRepository.findByPostIdAndUserId(postId, currentUserId);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        }


        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);
        return true;
    }

    public Long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
    public List<LikeResponse> getLikes(Long postId) {
        return likeRepository.findByPostId(postId)
                .stream()
                .map(like -> LikeResponse.builder()
                        .userId(like.getUser().getId())
                        .username(like.getUser().getUsername())
                        .build())
                .toList();
    }
}
