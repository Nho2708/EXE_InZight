package org.inzight.dto.request;

import lombok.Data;

@Data
public class RetirementInput {
    public double currentSavings;
    public double monthlyExpense;
    public double monthlySavings; // Monthly contribution
    public double inflationRate;   // Annual inflation rate (e.g., 0.03 for 3%)
    public String investmentType;
    public int ageNow;       // tuổi hiện tại
    public int retireAge;   // muốn nghỉ hưu lúc bao nhiêu tuổi
    
    // Default constructor for backward compatibility
    public RetirementInput() {
        this.monthlySavings = 0;
        this.inflationRate = 0.03; // Default 3% inflation
    }
}

