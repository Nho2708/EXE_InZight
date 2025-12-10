package org.inzight.service.SocialService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.PostRequest;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.*;
import org.inzight.enums.TransactionType;
import org.inzight.exception.AppException;
import org.inzight.exception.ErrorCode;
import org.inzight.repository.LikeRepository;
import org.inzight.repository.PostRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.inzight.service.FileStorageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.inzight.mapper.PostMapper;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor

public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostMapper postMapper;
    private final FileStorageService fileStorageService;
    private final AuthUtil authUtil;

    public List<PostResponse> getAll(UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> mapPostResponse(post, currentUser))
                .toList();
    }
    private PostResponse mapPostResponse(Post post, User currentUser) {
        PostResponse response = postMapper.toResponse(post);
        response.setLiked(likeRepository.existsByPostAndUser(post, currentUser));
        response.setLikeCount((int) likeRepository.countByPostId(post.getId()).intValue());
        return response;
    }

    @Transactional
    public PostResponse createPost(PostRequest request, MultipartFile imageFile) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Upload ảnh nếu có
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                // Kiểm tra xem có phải là URI content:// không (từ client cũ)
                String requestImageUrl = request.getImageUrl();
                if (requestImageUrl != null && 
                    (requestImageUrl.startsWith("http://") || requestImageUrl.startsWith("https://"))) {
                    // Nếu đã là URL hợp lệ, giữ nguyên
                    imageUrl = requestImageUrl;
                } else {
                    // Upload file mới
                    imageUrl = fileStorageService.uploadFile(imageFile, "posts");
                }
            } else if (request.getImageUrl() != null && 
                       (request.getImageUrl().startsWith("http://") || request.getImageUrl().startsWith("https://"))) {
                // Nếu không có file nhưng có URL hợp lệ trong request, dùng URL đó
                imageUrl = request.getImageUrl();
            }
            // Nếu là URI content:// hoặc file://, bỏ qua (không hợp lệ)

            Post post = Post.builder()
                    .user(currentUser)
                    .content(request.getContent())
                    .imageUrl(imageUrl)
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
    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile imageFile) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!post.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Post does not belong to you");
            }

            post.setContent(request.getContent());

            // Xử lý ảnh: nếu có file mới thì upload, nếu không thì giữ nguyên hoặc dùng URL từ request
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có (tùy chọn)
                if (post.getImageUrl() != null && post.getImageUrl().contains("/api/files/posts/")) {
                    try {
                        String oldFilename = post.getImageUrl().substring(post.getImageUrl().lastIndexOf("/") + 1);
                        fileStorageService.deleteFile("posts", oldFilename);
                    } catch (Exception e) {
                        log.warn("Could not delete old image", e);
                    }
                }
                // Upload ảnh mới
                post.setImageUrl(fileStorageService.uploadFile(imageFile, "posts"));
            } else if (request.getImageUrl() != null && 
                       (request.getImageUrl().startsWith("http://") || request.getImageUrl().startsWith("https://"))) {
                // Nếu không có file mới nhưng có URL hợp lệ trong request, cập nhật URL
                post.setImageUrl(request.getImageUrl());
            }
            // Nếu không có gì, giữ nguyên ảnh cũ

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


