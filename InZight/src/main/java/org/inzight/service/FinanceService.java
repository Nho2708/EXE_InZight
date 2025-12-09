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
        int years = req.retireAge - req.ageNow;
        if (years < 0) {
            years = 0;   // tránh lỗi nếu nhập sai (đã tới tuổi nghỉ hưu)
        }

        double yearlyExpense = req.monthlyExpense * 12;
        double yearlyReturn = investmentReturns.getOrDefault(req.investmentType, 0.05);

        // Lãi kép trong số năm còn lại
        double futureValue = req.currentSavings * Math.pow(1 + yearlyReturn, years);

        // Số năm có thể sống sau khi nghỉ hưu (chưa tính lạm phát)
        int sustainableYears = (int) (futureValue / yearlyExpense);

        return new RetirementResult(futureValue, sustainableYears);
    }


    // Scenario simulation
    public ScenarioResult calcScenario(ScenarioInput req) {
        double[] preset = scenarioPresets.get(req.preset);
        double incomeRate = preset[0];
        double expenseRate = preset[1];


        double newIncome = req.income * (1 + incomeRate);
        double newExpense = req.expense * (1 + expenseRate);


        return new ScenarioResult(newIncome, newExpense);
    }


    // What-if
    public WhatIfResult calcWhatIf(WhatIfInput req) {
        double newIncome = req.income * (1 + req.incomeChange);
        double newExpense = req.expense * (req.incomeChange < 0 ? 0.8 : 1.1);
        return new WhatIfResult(newIncome, newExpense);
    }


    // Multi-goal
    public GoalResult calcGoal(GoalInput req) {
        int months = (int) Math.ceil(req.targetAmount / req.monthlySaving);
        return new GoalResult(req.name, months);
    }
}
