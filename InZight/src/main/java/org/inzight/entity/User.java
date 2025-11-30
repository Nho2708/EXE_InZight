package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;
import org.inzight.enums.RoleName;


import java.time.LocalDate;
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


    @Column(name = "avatar_url",columnDefinition = "LONGTEXT")
    private String avatarUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoleName role; // USER hoặc ADMIN

    @Column(name = "gender", length = 20)
    private String gender;

    // Relations (optional mapped lists for convenience)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

}