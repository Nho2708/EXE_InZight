package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;
import org.inzight.enums.FriendStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "friends",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_friend_pair", columnNames = {"user_id", "friend_id"})
        },
        indexes = {
                @Index(name = "idx_friend_user", columnList = "user_id"),
                @Index(name = "idx_friend_friend", columnList = "friend_id")
        })
public class Friend extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FriendStatus status = FriendStatus.PENDING;
}