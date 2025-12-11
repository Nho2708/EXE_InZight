package org.inzight.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class RetirementResult {

    public double futureValue;
    public int sustainableYears;
    public double monthlySavingsNeeded;
    public double retirementGap;
    public double totalNeeded;
    public List<YearlyProjection> yearlyProjections;
    
    public RetirementResult(double futureValue, int sustainableYears) {
        this.futureValue = futureValue;
        this.sustainableYears = sustainableYears;
    }
    
    public RetirementResult(double futureValue, int sustainableYears, double monthlySavingsNeeded, 
                           double retirementGap, double totalNeeded, List<YearlyProjection> yearlyProjections) {
        this.futureValue = futureValue;
        this.sustainableYears = sustainableYears;
        this.monthlySavingsNeeded = monthlySavingsNeeded;
        this.retirementGap = retirementGap;
        this.totalNeeded = totalNeeded;
        this.yearlyProjections = yearlyProjections;
    }
    
    @Data
    public static class YearlyProjection {
        public int year;
        public double savings;
        public double expense;
        public double net;
        
        public YearlyProjection(int year, double savings, double expense, double net) {
            this.year = year;
            this.savings = savings;
            this.expense = expense;
            this.net = net;
        }
    }
}
