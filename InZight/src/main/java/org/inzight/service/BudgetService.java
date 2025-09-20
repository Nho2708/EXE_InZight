package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Budget;
import org.inzight.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget budget) {
        Budget existing = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        existing.setCategory(budget.getCategory());
        existing.setAmountLimit(budget.getAmountLimit());
        existing.setStartDate(budget.getStartDate());
        existing.setEndDate(budget.getEndDate());
        return budgetRepository.save(existing);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}