package org.inzight.repository;

import org.inzight.entity.Comment;
import org.inzight.entity.CommentLike;
import org.inzight.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentAndUser(Comment comment, User user);

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    long countByComment(Comment comment);
}
