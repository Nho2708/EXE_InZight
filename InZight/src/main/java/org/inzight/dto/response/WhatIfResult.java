package org.inzight.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class WhatIfResult {
    public double newIncome;
    public double adjustedExpense;
    public double netSavings;
    public double improvement;
    public List<YearlyProjection> yearlyProjections;
    
    public WhatIfResult(double newIncome, double adjustedExpense) {
        this.newIncome = newIncome;
        this.adjustedExpense = adjustedExpense;
        this.netSavings = newIncome - adjustedExpense;
        this.improvement = 0;
    }
    
    public WhatIfResult(double newIncome, double adjustedExpense, double netSavings, 
                       double improvement, List<YearlyProjection> yearlyProjections) {
        this.newIncome = newIncome;
        this.adjustedExpense = adjustedExpense;
        this.netSavings = netSavings;
        this.improvement = improvement;
        this.yearlyProjections = yearlyProjections;
    }
    
    @Data
    public static class YearlyProjection {
        public int year;
        public double income;
        public double expense;
        public double savings;
        public double cumulativeSavings;
        
        public YearlyProjection(int year, double income, double expense, double savings, double cumulativeSavings) {
            this.year = year;
            this.income = income;
            this.expense = expense;
            this.savings = savings;
            this.cumulativeSavings = cumulativeSavings;
        }
    }
}
