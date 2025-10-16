package org.inzight.service.SocialService;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Comment;
import org.inzight.entity.CommentLike;
import org.inzight.entity.User;
import org.inzight.exception.AppException;
import org.inzight.exception.ErrorCode;
import org.inzight.repository.CommentLikeRepository;
import org.inzight.repository.CommentRepository;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public String toggleLike(Long commentId) {
        Long currentUserId = authUtil.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        return commentLikeRepository.findByCommentAndUser(comment, user)
                .map(existing -> {
                    commentLikeRepository.delete(existing);
                    return "unliked";
                })
                .orElseGet(() -> {
                    CommentLike like = CommentLike.builder()
                            .comment(comment)
                            .user(user)
                            .build();
                    commentLikeRepository.save(like);
                    return "liked";
                });
    }

    public long countLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        return commentLikeRepository.countByComment(comment);
    }
}
