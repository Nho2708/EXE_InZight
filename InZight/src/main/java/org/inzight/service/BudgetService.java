package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.BudgetRequest;
import org.inzight.dto.response.BudgetResponse;
import org.inzight.entity.Budget;
import org.inzight.exception.AppException;
import org.inzight.exception.ErrorCode;
import org.inzight.mapper.BudgetMapper;
import org.inzight.repository.BudgetRepository;
import org.inzight.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    public BudgetResponse create(BudgetRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Budget budget = budgetMapper.toEntity(request);
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    public BudgetResponse update(Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        budgetMapper.updateEntity(budget, request);
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    public void delete(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
        budgetRepository.delete(budget);
    }

    public BudgetResponse getById(Long id) {
        return budgetRepository.findById(id)
                .map(budgetMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
    }

    public List<BudgetResponse> getByUser(Long userId) {
        return budgetRepository.findByUserId(userId)
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }
}
