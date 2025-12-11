package org.inzight.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ScenarioResult {
    public double adjustedIncome;
    public double adjustedExpense;
    public double netChange;
    public String preset;
    public String presetName;
    public List<YearlyProjection> yearlyProjections;
    
    public ScenarioResult(double adjustedIncome, double adjustedExpense) {
        this.adjustedIncome = adjustedIncome;
        this.adjustedExpense = adjustedExpense;
        this.netChange = adjustedIncome - adjustedExpense;
    }
    
    public ScenarioResult(double adjustedIncome, double adjustedExpense, double netChange,
                        String preset, String presetName, List<YearlyProjection> yearlyProjections) {
        this.adjustedIncome = adjustedIncome;
        this.adjustedExpense = adjustedExpense;
        this.netChange = netChange;
        this.preset = preset;
        this.presetName = presetName;
        this.yearlyProjections = yearlyProjections;
    }
    
    @Data
    public static class YearlyProjection {
        public int year;
        public double income;
        public double expense;
        public double net;
        public double cumulativeImpact;
        
        public YearlyProjection(int year, double income, double expense, double net, double cumulativeImpact) {
            this.year = year;
            this.income = income;
            this.expense = expense;
            this.net = net;
            this.cumulativeImpact = cumulativeImpact;
        }
    }
}
