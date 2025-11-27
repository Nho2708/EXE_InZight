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
import org.inzight.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;
    private final SecurityUtil securityUtil;

    public BudgetResponse create(BudgetRequest request) {

        // USER chỉ được tạo budget cho chính họ
        Long currentUserId = securityUtil.getCurrentUserId();
        request.setUserId(currentUserId);

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Budget budget = budgetMapper.toEntity(request);
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    public BudgetResponse update(Long id, BudgetRequest request) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        Long currentUserId = securityUtil.getCurrentUserId();

        // USER chỉ được sửa budget của chính họ
        if (!securityUtil.isAdmin() && !budget.getUserId().equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        budgetMapper.updateEntity(budget, request);
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    public void delete(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        Long currentUserId = securityUtil.getCurrentUserId();

        if (!securityUtil.isAdmin() && !budget.getUserId().equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        budgetRepository.delete(budget);
    }

    public BudgetResponse getById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        Long currentUserId = securityUtil.getCurrentUserId();

        if (!securityUtil.isAdmin() && !budget.getUserId().equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return budgetMapper.toResponse(budget);
    }

    public List<BudgetResponse> getByUser(Long userId) {

        Long currentUserId = securityUtil.getCurrentUserId();

        // USER chỉ xem budget của chính họ
        if (!securityUtil.isAdmin() && !userId.equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return budgetRepository.findByUserId(userId)
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }
}
