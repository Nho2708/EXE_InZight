package org.inzight.repository;


import org.inzight.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    Long countByPostId(Long postId);
    List<Like> findByPostId(Long postId);
}
