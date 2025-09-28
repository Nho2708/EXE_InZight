package org.inzight.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BudgetRequest {
    Long userId;
    Long categoryId;
    String budgetName;
    BigDecimal amountLimit;
    LocalDate startDate;
    LocalDate endDate;
}
