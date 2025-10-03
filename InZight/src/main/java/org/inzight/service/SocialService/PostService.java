package org.inzight.service.SocialService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.PostRequest;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.*;
import org.inzight.enums.TransactionType;
import org.inzight.repository.PostRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;
import org.inzight.mapper.PostMapper;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor

public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    private final AuthUtil authUtil;

    public List<PostResponse> getAll() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = Post.builder()
                    .user(currentUser)
                    .content(request.getContent())
                    .imageUrl(request.getImageUrl())
                    .build();

            Post saved = postRepository.save(post);
            return postMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Error creating post", e);
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }

    // Cập nhật post
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!post.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Post does not belong to you");
            }

            post.setContent(request.getContent());
            post.setImageUrl(request.getImageUrl());

            Post updated = postRepository.save(post);
            return postMapper.toResponse(updated);

        } catch (Exception e) {
            log.error("Error updating post", e);
            throw new RuntimeException("Failed to update post: " + e.getMessage(), e);
        }
    }

    // Xóa post
    @Transactional
    public void deletePost(Long postId) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!post.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Post does not belong to you");
            }

            postRepository.delete(post);

        } catch (Exception e) {
            log.error("Error deleting post", e);
            throw new RuntimeException("Failed to delete post: " + e.getMessage(), e);
        }
    }

    // Lấy post theo id
    public PostResponse getById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return postMapper.toResponse(post);
    }
    public List<PostResponse> getMyPosts() {
        Long currentUserId = authUtil.getCurrentUserId();
        List<Post> posts = postRepository.findByUserId(currentUserId);
        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }
}


