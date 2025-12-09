package org.inzight.dto.response;

import lombok.Data;

@Data
public class ScenarioResult {
    public double adjustedIncome;
    public double adjustedExpense;
    public ScenarioResult(double adjustedIncome, double adjustedExpense) {
        this.adjustedIncome = adjustedIncome;
        this.adjustedExpense = adjustedExpense;
    }
}
