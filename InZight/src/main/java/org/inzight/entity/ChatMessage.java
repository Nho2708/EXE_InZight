package org.inzight.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_sender", columnList = "sender_id"),
                @Index(name = "idx_chat_receiver", columnList = "receiver_id"),
                @Index(name = "idx_chat_created", columnList = "created_at")
        })
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.time.Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = java.time.Instant.now();
    }
}