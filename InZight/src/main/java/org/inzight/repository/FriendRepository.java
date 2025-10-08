package org.inzight.repository;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Friend;
import org.inzight.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserIdOrFriendId(Long userId, Long friendId);
    boolean existsByUserAndFriend(User user, User friend);
    Optional<Friend> findByUserIdAndFriendIdOrFriendIdAndUserId(
            Long userId, Long friendId, Long friendId2, Long userId2);
    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
}
