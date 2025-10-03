package org.inzight.service.SocialService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.CommentRequest;
import org.inzight.dto.request.PostRequest;
import org.inzight.dto.response.CommentResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Comment;
import org.inzight.entity.Post;
import org.inzight.entity.User;
import org.inzight.mapper.CommentMapper;
import org.inzight.repository.CommentRepository;
import org.inzight.repository.PostRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<CommentResponse> getAll() {
        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
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
            return commentMapper.toResponse(saved);

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
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            if (!comment.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Comment does not belong to you");
            }

            comment.setContent(request.getContent());


            Comment update = commentRepository.save(comment);
            return commentMapper.toResponse(update);

        } catch (Exception e) {
            log.error("Error creating comment", e);
            throw new RuntimeException("Failed to add Comment: " + e.getMessage(), e);
        }
    }
    public List<CommentResponse> getMyComments() {
        Long currentUserId = authUtil.getCurrentUserId();
        List<Comment> comments = commentRepository.findByUserId(currentUserId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
    }
    public CommentResponse getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Cmt not found"));
        return commentMapper.toResponse(comment);
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
            throw new RuntimeException("Failed to delete cmt: " + e.getMessage(), e);
        }
    }






}
