package org.inzight.dto.request;

import lombok.Data;

@Data
public class WhatIfInput {
    public double income;
    public double expense;
    public double incomeChange;
    public double inflationRate;  // Annual inflation rate
    public String returnRate;      // SAFE, MODERATE, HIGH
    public int projectionYears;    // Number of years to project
    
    public WhatIfInput() {
        this.inflationRate = 0.03; // Default 3%
        this.projectionYears = 15;
    }
}
