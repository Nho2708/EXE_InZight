package org.inzight.dto.request;

import lombok.Data;

@Data
public class GoalInput {
    public String name;
    public double targetAmount;
    public double monthlySaving;
    public double interestRate;  // Annual interest rate (e.g., 0.05 for 5%)
    public int priority;         // 1 = High, 2 = Medium, 3 = Low
    public String targetDate;    // Optional target date
    
    public GoalInput() {
        this.interestRate = 0.05; // Default 5% annual interest
        this.priority = 2; // Default medium priority
    }
}
