package org.inzight.repository;


import org.inzight.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByCreatedAtAsc(Long senderId, Long receiverId);
    List<ChatMessage> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<ChatMessage> findByReceiverIdAndSenderIdOrderByCreatedAtAsc(Long receiverId, Long senderId);
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :userId AND m.receiver.id = :receiverId) OR " +
            "(m.sender.id = :receiverId AND m.receiver.id = :userId) " +
            "ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatBetween(@Param("userId") Long userId, @Param("receiverId") Long receiverId);

}