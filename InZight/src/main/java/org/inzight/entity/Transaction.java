package org.inzight.entity;



import jakarta.persistence.*;
import lombok.*;
import org.inzight.enums.TransactionType;


import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_tx_wallet", columnList = "wallet_id"),
                @Index(name = "idx_tx_category", columnList = "category_id"),
                @Index(name = "idx_tx_date", columnList = "transaction_date")
        })
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type; // INCOME | EXPENSE


    @Column(length = 255)
    private String note;


    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;


    @PrePersist
    public void prePersist() {
        if (transactionDate == null) transactionDate = Instant.now();
    }
}