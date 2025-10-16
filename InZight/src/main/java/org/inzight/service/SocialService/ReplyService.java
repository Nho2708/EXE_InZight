package org.inzight.service.SocialService;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ReplyRequest;
import org.inzight.dto.response.ReplyResponse;
import org.inzight.entity.Comment;
import org.inzight.entity.Reply;
import org.inzight.entity.User;
import org.inzight.exception.AppException;
import org.inzight.exception.ErrorCode;
import org.inzight.mapper.ReplyMapper;
import org.inzight.repository.CommentRepository;
import org.inzight.repository.ReplyRepository;
import org.inzight.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReplyMapper replyMapper;

    public ReplyResponse addReply(UserDetails userDetails, ReplyRequest request) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = Reply.builder()
                .user(user)
                .comment(comment)
                .content(request.getContent())
                .build();

        replyRepository.save(reply);
        return replyMapper.toResponse(reply);
    }

    public List<ReplyResponse> getRepliesByComment(Long commentId) {
        return replyRepository.findByCommentId(commentId)
                .stream()
                .map(replyMapper::toResponse)
                .toList();
    }

    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }
}
