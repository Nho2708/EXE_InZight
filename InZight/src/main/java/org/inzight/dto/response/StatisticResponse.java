package org.inzight.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal compareLastMonth;
    private List<CategoryStatistic> categories;
}
