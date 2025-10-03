package org.inzight.repository;


import org.inzight.entity.Like;
import org.inzight.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository  extends JpaRepository<Share, Long> {
    Long countByPostId(Long postId);

    List<Share> findByPostId(Long postId);
    Optional<Share> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
