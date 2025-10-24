package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;
import org.inzight.enums.CategoryType;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type; // INCOME | EXPENSE

    // Thêm cột iconUrl
    @Column(name = "icon_url", length = 255)
    private String iconUrl;
}