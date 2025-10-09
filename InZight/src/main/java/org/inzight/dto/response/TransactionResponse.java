package org.inzight.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String categoryName;
    private String walletName;
    private BigDecimal amount;
    private String type; // "INCOME" | "EXPENSE"
    private String note;
    private LocalDateTime transactionDate;
}
