package org.inzight.dto.request;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private Long walletId;
    private Long categoryId;
    private BigDecimal amount;
    private String type;   // "INCOME" | "EXPENSE"
    private String note;
}
