package org.inzight.entity;



import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_post", columnList = "post_id"),
                @Index(name = "idx_comments_user", columnList = "user_id")
        })
public class Comment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)

    private Post post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Lob
    @Column(nullable = false)
    private String content;
}