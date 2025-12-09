package org.inzight.dto.response;

import lombok.Data;

@Data
public class WhatIfResult {
    public double newIncome;
    public double adjustedExpense;
    public WhatIfResult(double newIncome, double adjustedExpense) {
        this.newIncome = newIncome;
        this.adjustedExpense = adjustedExpense;
    }
}
