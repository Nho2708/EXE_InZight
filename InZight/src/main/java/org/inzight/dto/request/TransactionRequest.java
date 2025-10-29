package org.inzight.dto.request;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private Long walletId;
    private Long categoryId;
    private BigDecimal amount;
    private String type;   // "INCOME" | "EXPENSE"
    private String note;
    private Instant transactionDate;
}
