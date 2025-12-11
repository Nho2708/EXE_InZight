package org.inzight.service;

import org.inzight.dto.request.GoalInput;
import org.inzight.dto.request.RetirementInput;
import org.inzight.dto.request.ScenarioInput;
import org.inzight.dto.request.WhatIfInput;
import org.inzight.dto.response.GoalResult;
import org.inzight.dto.response.RetirementResult;
import org.inzight.dto.response.ScenarioResult;
import org.inzight.dto.response.WhatIfResult;

import java.util.Map;

public class FinanceService {
    private static final Map<String, Double> investmentReturns = Map.of(
            "tietkiem", 0.05,
            "traiphieu", 0.08,
            "etf", 0.085,
            "chungkhoan", 0.13,
            "bds", 0.08
    );


    private static final Map<String, double[]> scenarioPresets = Map.of(
            "inflation_low", new double[]{0, 0.03}, // income, expense
            "inflation_high", new double[]{0, 0.10},
            "crisis_light", new double[]{-0.10, 0.02},
            "crisis_medium", new double[]{-0.30, 0.05},
            "crisis_severe", new double[]{-0.50, 0.10},
            "pandemic", new double[]{-0.40, 0.15}
    );


    // Retirement calculation
    public RetirementResult calcRetirement(RetirementInput req) {
        // Validation
        if (req.ageNow < 18 || req.ageNow > 100) {
            throw new IllegalArgumentException("Tuổi hiện tại phải từ 18-100");
        }
        if (req.retireAge <= req.ageNow) {
            throw new IllegalArgumentException("Tuổi nghỉ hưu phải lớn hơn tuổi hiện tại");
        }
        if (req.monthlyExpense <= 0) {
            throw new IllegalArgumentException("Chi tiêu hàng tháng phải lớn hơn 0");
        }
        if (req.currentSavings < 0) {
            throw new IllegalArgumentException("Tiết kiệm hiện tại không được âm");
        }
        
        int years = req.retireAge - req.ageNow;
        double yearlyReturn = investmentReturns.getOrDefault(req.investmentType, 0.05);
        double monthlyReturn = yearlyReturn / 12;
        double inflationRate = req.inflationRate > 0 ? req.inflationRate : 0.03; // Default 3%
        
        // Calculate future value
        // Step 1: Future value of current savings (with compound interest)
        // FV_current = PV × (1 + r)^n
        double futureValueFromSavings = req.currentSavings * Math.pow(1 + yearlyReturn, years);
        
        // Step 2: Future value of monthly contributions (SIMPLE calculation, NO compound interest)
        // FV_contributions = Monthly Amount × 12 months × years
        // This is a simple sum, not compound interest calculation
        double monthlyContribution = req.monthlySavings > 0 ? req.monthlySavings : 0;
        double futureValueFromContributions = 0;
        if (monthlyContribution > 0) {
            futureValueFromContributions = monthlyContribution * 12 * years;
        }
        
        // Step 3: Total future value at retirement
        double totalFutureValue = futureValueFromSavings + futureValueFromContributions;
        
        // Step 4: Calculate annual expense at retirement (adjusted for inflation)
        // Monthly expense grows with inflation over the years to retirement
        double monthlyExpenseAtRetirement = req.monthlyExpense * Math.pow(1 + inflationRate, years);
        double yearlyExpenseAtRetirement = monthlyExpenseAtRetirement * 12;
        
        // Step 5: Calculate total needed for retirement (20 years post-retirement)
        // Each year's expense grows with inflation and is discounted by return rate
        int yearsAfterRetirement = 20;
        double totalNeeded = 0;
        for (int i = 0; i < yearsAfterRetirement; i++) {
            // Expense in year i (grows with inflation)
            double expenseInYear = yearlyExpenseAtRetirement * Math.pow(1 + inflationRate, i);
            // Discount back to retirement age using return rate
            totalNeeded += expenseInYear / Math.pow(1 + yearlyReturn, i);
        }
        
        // Calculate sustainable years (considering inflation each year)
        int sustainableYears = 0;
        double tempFutureValue = totalFutureValue;
        double currentYearlyExpense = yearlyExpenseAtRetirement;
        while (tempFutureValue > 0 && sustainableYears < 100) { // Cap at 100 years to prevent infinite loop
            tempFutureValue -= currentYearlyExpense;
            currentYearlyExpense *= (1 + inflationRate); // Expense continues to inflate
            sustainableYears++;
        }
        
        // Calculate retirement gap
        double retirementGap = totalNeeded - totalFutureValue;
        if (retirementGap < 0) retirementGap = 0;
        
        // Step 8: Calculate monthly savings needed to close the gap
        // This uses compound interest formula because we need to calculate how much to save
        // monthly to reach the gap amount with compound interest
        double monthlySavingsNeeded = 0;
        if (retirementGap > 0 && years > 0) {
            // PMT = FV × r / ((1 + r)^n - 1)
            // Where: FV = gap, r = monthly rate, n = total months
            double monthlyRate = yearlyReturn / 12;
            double totalMonths = years * 12;
            double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
            if (denominator > 0) {
                monthlySavingsNeeded = retirementGap * monthlyRate / denominator;
            }
        }
        
        // Generate yearly projections for first 5 years after retirement
        java.util.List<RetirementResult.YearlyProjection> projections = new java.util.ArrayList<>();
        double remainingSavings = totalFutureValue;
        for (int i = 0; i < Math.min(5, sustainableYears); i++) {
            double expenseInYear = yearlyExpenseAtRetirement * Math.pow(1 + inflationRate, i);
            remainingSavings -= expenseInYear;
            if (remainingSavings < 0) remainingSavings = 0;
            projections.add(new RetirementResult.YearlyProjection(
                req.retireAge + i, remainingSavings, expenseInYear, remainingSavings - expenseInYear
            ));
        }
        
        return new RetirementResult(
            totalFutureValue, 
            sustainableYears, 
            monthlySavingsNeeded,
            retirementGap,
            totalNeeded,
            projections
        );
    }


    // Scenario simulation
    public ScenarioResult calcScenario(ScenarioInput req) {
        // Validation
        if (req.income <= 0 || req.expense <= 0) {
            throw new IllegalArgumentException("Thu nhập và chi tiêu phải lớn hơn 0");
        }
        if (!scenarioPresets.containsKey(req.preset)) {
            throw new IllegalArgumentException("Scenario preset không hợp lệ: " + req.preset);
        }
        
        double[] preset = scenarioPresets.get(req.preset);
        double incomeRate = preset[0];
        double expenseRate = preset[1];
        int projectionYears = req.projectionYears > 0 ? req.projectionYears : 10;
        
        // Get preset name
        String presetName = getPresetName(req.preset);
        
        // Calculate adjusted values
        double newIncome = req.income * (1 + incomeRate);
        double newExpense = req.expense * (1 + expenseRate);
        double originalNet = req.income - req.expense;
        double newNet = newIncome - newExpense;
        double netChange = newNet - originalNet;
        
        // Generate yearly projections
        java.util.List<ScenarioResult.YearlyProjection> projections = new java.util.ArrayList<>();
        double cumulativeImpact = 0;
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        
        for (int year = 0; year <= projectionYears; year++) {
            // Apply scenario effects over time (effects may compound or stabilize)
            double yearIncome = req.income * Math.pow(1 + incomeRate, year);
            double yearExpense = req.expense * Math.pow(1 + expenseRate, year);
            double yearNet = yearIncome - yearExpense;
            
            cumulativeImpact += (yearNet - originalNet);
            
            if (year % 2 == 0 || year == projectionYears) { // Sample every 2 years
                projections.add(new ScenarioResult.YearlyProjection(
                    currentYear + year,
                    yearIncome,
                    yearExpense,
                    yearNet,
                    cumulativeImpact
                ));
            }
        }
        
        return new ScenarioResult(newIncome, newExpense, netChange, req.preset, presetName, projections);
    }
    
    private String getPresetName(String preset) {
        switch (preset) {
            case "inflation_low": return "Low Inflation";
            case "inflation_high": return "High Inflation";
            case "crisis_light": return "Light Crisis";
            case "crisis_medium": return "Medium Crisis";
            case "crisis_severe": return "Severe Crisis";
            case "pandemic": return "Pandemic";
            default: return "Default Scenario";
        }
    }


    // What-if (Optimizer)
    public WhatIfResult calcWhatIf(WhatIfInput req) {
        // Validation
        if (req.income <= 0 || req.expense <= 0) {
            throw new IllegalArgumentException("Thu nhập và chi tiêu phải lớn hơn 0");
        }
        
        double inflationRate = req.inflationRate > 0 ? req.inflationRate : 0.03;
        int projectionYears = req.projectionYears > 0 ? req.projectionYears : 15;
        
        // Determine return rate based on strategy
        double returnRate = 0.05; // Default safe
        if ("HIGH".equals(req.returnRate)) {
            returnRate = 0.10; // 10% for high risk
        } else if ("MODERATE".equals(req.returnRate)) {
            returnRate = 0.07; // 7% for moderate
        } else {
            returnRate = 0.05; // 5% for safe
        }
        
        // Calculate optimized income growth
        double incomeGrowthRate = returnRate; // Income grows with investment return
        double newIncome = req.income * (1 + req.incomeChange);
        
        // Calculate expense adjustment (more controlled growth)
        double expenseGrowthRate = inflationRate * 0.8; // Expense grows slower than inflation
        double adjustedExpense = req.expense * (1 + expenseGrowthRate);
        
        // Calculate net savings
        double originalNet = req.income - req.expense;
        double newNet = newIncome - adjustedExpense;
        double improvement = ((newNet - originalNet) / originalNet) * 100;
        
        // Generate yearly projections with compound interest
        java.util.List<WhatIfResult.YearlyProjection> projections = new java.util.ArrayList<>();
        double cumulativeSavings = 0;
        
        for (int year = 0; year <= projectionYears; year++) {
            // Current path (no optimization)
            double currentYearIncome = req.income * Math.pow(1 + inflationRate, year);
            double currentYearExpense = req.expense * Math.pow(1 + inflationRate, year);
            double currentYearSavings = currentYearIncome - currentYearExpense;
            
            // Optimized path
            double optimizedYearIncome = newIncome * Math.pow(1 + incomeGrowthRate, year);
            double optimizedYearExpense = adjustedExpense * Math.pow(1 + expenseGrowthRate, year);
            double optimizedYearSavings = optimizedYearIncome - optimizedYearExpense;
            
            // Apply compound interest to savings
            cumulativeSavings = cumulativeSavings * (1 + returnRate) + optimizedYearSavings;
            
            if (year % 3 == 0 || year == projectionYears) { // Sample every 3 years
                projections.add(new WhatIfResult.YearlyProjection(
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + year,
                    optimizedYearIncome,
                    optimizedYearExpense,
                    optimizedYearSavings,
                    cumulativeSavings
                ));
            }
        }
        
        return new WhatIfResult(newIncome, adjustedExpense, newNet, improvement, projections);
    }


    // Multi-goal
    public GoalResult calcGoal(GoalInput req) {
        // Validation
        if (req.name == null || req.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên mục tiêu không được để trống");
        }
        if (req.targetAmount <= 0) {
            throw new IllegalArgumentException("Số tiền mục tiêu phải lớn hơn 0");
        }
        if (req.monthlySaving <= 0) {
            throw new IllegalArgumentException("Số tiền tiết kiệm hàng tháng phải lớn hơn 0");
        }
        if (req.monthlySaving >= req.targetAmount) {
            throw new IllegalArgumentException("Số tiền tiết kiệm hàng tháng phải nhỏ hơn số tiền mục tiêu");
        }
        
        double interestRate = req.interestRate > 0 ? req.interestRate : 0.05;
        double monthlyRate = interestRate / 12;
        int priority = req.priority > 0 && req.priority <= 3 ? req.priority : 2;
        
        // Calculate months needed with compound interest
        // FV = PMT * [((1 + r)^n - 1) / r]
        // Solving for n: n = log(1 + FV*r/PMT) / log(1 + r)
        int months;
        if (monthlyRate > 0) {
            double factor = 1 + (req.targetAmount * monthlyRate / req.monthlySaving);
            months = (int) Math.ceil(Math.log(factor) / Math.log(1 + monthlyRate));
        } else {
            // No interest case
            months = (int) Math.ceil(req.targetAmount / req.monthlySaving);
        }
        
        int yearsNeeded = months / 12;
        
        // Calculate total with interest
        double totalWithInterest = 0;
        if (monthlyRate > 0) {
            totalWithInterest = req.monthlySaving * 
                (Math.pow(1 + monthlyRate, months) - 1) / monthlyRate;
        } else {
            totalWithInterest = req.monthlySaving * months;
        }
        
        double interestEarned = totalWithInterest - (req.monthlySaving * months);
        if (interestEarned < 0) interestEarned = 0;
        
        // Calculate target date
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, months);
        String targetDate = String.format("%d/%d/%d", 
            cal.get(java.util.Calendar.DAY_OF_MONTH),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.YEAR));
        
        // Generate milestones (every 25% progress)
        java.util.List<GoalResult.Milestone> milestones = new java.util.ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            int milestoneMonth = (months * i) / 4;
            double milestoneAmount = req.monthlySaving * milestoneMonth;
            if (monthlyRate > 0) {
                milestoneAmount = req.monthlySaving * 
                    (Math.pow(1 + monthlyRate, milestoneMonth) - 1) / monthlyRate;
            }
            double progress = (milestoneAmount / req.targetAmount) * 100;
            if (progress > 100) progress = 100;
            
            String description = String.format("Đạt %d%% mục tiêu", i * 25);
            milestones.add(new GoalResult.Milestone(milestoneMonth, milestoneAmount, progress, description));
        }
        
        return new GoalResult(req.name, months, yearsNeeded, totalWithInterest, 
                            interestEarned, targetDate, milestones, priority);
    }
}
