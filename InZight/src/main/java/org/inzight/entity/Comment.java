package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    // Quan hệ với Post (nhiều comment thuộc 1 post)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Quan hệ với User (nhiều comment thuộc 1 user)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Nội dung comment
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Khi xoá comment, xoá luôn các like
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    // Giúp quản lý 2 chiều (khi thêm like)
    public void addLike(CommentLike like) {
        likes.add(like);
        like.setComment(this);
    }

    public void removeLike(CommentLike like) {
        likes.remove(like);
        like.setComment(null);
    }
}
