package org.inzight.service.SocialService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.CommentRequest;
import org.inzight.dto.response.CommentResponse;
import org.inzight.entity.Comment;
import org.inzight.entity.Post;
import org.inzight.entity.User;
import org.inzight.mapper.CommentMapper;
import org.inzight.repository.*;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository; // 👈 Thêm repository này

    // Lấy tất cả comment (ít dùng)
    public List<CommentResponse> getAll() {
        Long currentUserId = authUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(comment -> mapCommentResponse(comment, currentUser))
                .toList();

    }

    // Lấy comment theo Post ID (chính dùng trong FE)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        Long currentUserId = authUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        return comments.stream().map(comment -> {
            CommentResponse response = commentMapper.toResponse(comment);

            // Đếm số lượt like
            response.setLikeCount((int) commentLikeRepository.countByComment(comment));

            // Kiểm tra người hiện tại đã like chưa
            response.setLiked(commentLikeRepository.existsByCommentAndUser(comment, currentUser));

            return response;
        }).toList();
    }


    // Hàm map tiện ích
    private CommentResponse mapCommentResponse(Comment comment, User currentUser) {
        CommentResponse response = commentMapper.toResponse(comment);
        response.setLikeCount((int) commentLikeRepository.countByComment(comment));
        response.setLiked(commentLikeRepository.existsByCommentAndUser(comment, currentUser));
        return response;
    }

    @Transactional
    public CommentResponse addComment(CommentRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Comment comment = Comment.builder()
                    .user(currentUser)
                    .content(request.getContent())
                    .post(post)
                    .build();

            Comment saved = commentRepository.save(comment);

            // 👇 Map sau khi lưu (có likeCount = 0 và liked = false)
            CommentResponse response = commentMapper.toResponse(saved);
            response.setLikeCount(0);
            response.setLiked(false);
            return response;

        } catch (Exception e) {
            log.error("Error creating comment", e);
            throw new RuntimeException("Failed to add Comment: " + e.getMessage(), e);
        }
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            if (!comment.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Comment does not belong to you");
            }

            comment.setContent(request.getContent());
            Comment update = commentRepository.save(comment);

            return mapCommentResponse(update, currentUser);

        } catch (Exception e) {
            log.error("Error updating comment", e);
            throw new RuntimeException("Failed to update Comment: " + e.getMessage(), e);
        }
    }

    public List<CommentResponse> getMyComments() {
        Long currentUserId = authUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Comment> comments = commentRepository.findByUserId(currentUserId);
        return comments.stream()
                .map(comment -> mapCommentResponse(comment, currentUser))
                .toList();

    }

    public CommentResponse getById(Long commentId) {
        Long currentUserId = authUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return mapCommentResponse(comment, currentUser);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            if (!comment.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Comment does not belong to you");
            }

            commentRepository.delete(comment);

        } catch (Exception e) {
            log.error("Error deleting comment", e);
            throw new RuntimeException("Failed to delete comment: " + e.getMessage(), e);
        }
    }
}
