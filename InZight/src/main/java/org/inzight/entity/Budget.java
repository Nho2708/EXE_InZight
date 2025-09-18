package org.inzight.entity;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budgets",
        indexes = {
                @Index(name = "idx_budget_user", columnList = "user_id"),
                @Index(name = "idx_budget_category", columnList = "category_id")
        })
public class Budget extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Column(name = "amount_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountLimit;


    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;


    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}