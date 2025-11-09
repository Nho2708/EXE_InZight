package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_like_post_user", columnNames = {"post_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_likes_post", columnList = "post_id"),
                @Index(name = "idx_likes_user", columnList = "user_id")
        }
)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết tới Post (khi Post bị xóa thì Like cũng bị xóa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Liên kết tới User (người đã Like)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
