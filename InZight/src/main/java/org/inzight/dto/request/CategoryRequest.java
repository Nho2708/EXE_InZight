package org.inzight.dto.request;
import jakarta.persistence.*;
import lombok.*;
import org.inzight.enums.CategoryType;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryid")
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING) // 👈 fix ENUM mapping
    @Column(name = "type", nullable = false)
    private CategoryType type;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}