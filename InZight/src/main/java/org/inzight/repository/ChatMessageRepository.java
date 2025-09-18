package org.inzight.repository;


import org.inzight.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByCreatedAtAsc(Long senderId, Long receiverId);
    List<ChatMessage> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}