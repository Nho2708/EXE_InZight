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
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_email", columnList = "email")
        })
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 50)
    private String username;


    @Column(nullable = false, unique = true, length = 100)
    private String email;


    @Column(nullable = false, length = 255)
    private String password;


    @Column(name = "full_name", length = 100)
    private String fullName;


    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;


    // Relations (optional mapped lists for convenience)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}