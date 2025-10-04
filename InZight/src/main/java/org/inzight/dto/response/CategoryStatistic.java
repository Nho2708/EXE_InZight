package org.inzight.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatistic {
    private String categoryName;
    private BigDecimal amount;
    private double percent;
}
