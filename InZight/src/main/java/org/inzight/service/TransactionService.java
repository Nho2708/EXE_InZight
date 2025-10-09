package org.inzight.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.TransactionRequest;
import org.inzight.dto.response.CategoryStatistic;
import org.inzight.dto.response.StatisticResponse;
import org.inzight.dto.response.TransactionResponse;
import org.inzight.entity.Category;
import org.inzight.entity.Transaction;
import org.inzight.entity.Wallet;
import org.inzight.enums.TransactionType;
import org.inzight.mapper.TransactionMapper;
import org.inzight.repository.CategoryRepository;
import org.inzight.repository.TransactionRepository;
import org.inzight.repository.WalletRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final AuthUtil authUtil;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            Wallet wallet = walletRepository.findById(request.getWalletId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            if (!wallet.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Wallet does not belong to you");
            }

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .category(category)
                    .amount(request.getAmount())
                    .type(TransactionType.valueOf(request.getType()))
                    .note(request.getNote())
                    .build();

            // Cập nhật số dư ví
            if (transaction.getType() == TransactionType.INCOME) {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }

            walletRepository.save(wallet);
            transactionRepository.save(transaction);

            return transactionMapper.toResponse(transaction);

        } catch (Exception e) {
            log.error("Error creating transaction", e);
            throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
        }
    }

    @Transactional
    public TransactionResponse updateTransaction(Long transactionId, TransactionRequest request) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();

            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            Wallet wallet = transaction.getWallet();
            if (!wallet.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Wallet does not belong to you");
            }

            // rollback balance
            if (transaction.getType() == TransactionType.INCOME) {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            }

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            transaction.setCategory(category);
            transaction.setAmount(request.getAmount());
            transaction.setType(TransactionType.valueOf(request.getType()));
            transaction.setNote(request.getNote());

            // apply new balance
            if (transaction.getType() == TransactionType.INCOME) {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }

            walletRepository.save(wallet);
            transactionRepository.save(transaction);

            return transactionMapper.toResponse(transaction);

        } catch (Exception e) {
            log.error("Error updating transaction", e);
            throw new RuntimeException("Failed to update transaction: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteTransaction(Long transactionId) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            Wallet wallet = transaction.getWallet();
            if (!wallet.getUser().getId().equals(currentUserId)) {
                throw new RuntimeException("Unauthorized: Wallet does not belong to you");
            }

            // rollback balance
            if (transaction.getType() == TransactionType.INCOME) {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            }

            walletRepository.save(wallet);
            transactionRepository.delete(transaction);

        } catch (Exception e) {
            log.error("Error deleting transaction", e);
            throw new RuntimeException("Failed to delete transaction: " + e.getMessage(), e);
        }
    }

    public List<TransactionResponse> getTransactionsByUserAndType(String type) {
        Long currentUserId = authUtil.getCurrentUserId();

        List<Transaction> transactions;
        if (type == null || type.isBlank()) {
            transactions = transactionRepository.findByWalletUserId(currentUserId);
        } else {
            TransactionType transactionType;
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid transaction type: " + type);
            }
            transactions = transactionRepository.findByWalletUserIdAndType(currentUserId, transactionType);
        }

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    public List<TransactionResponse> getTransactions() {
        Long currentUserId = authUtil.getCurrentUserId();
        return transactionRepository.findByWalletUserId(currentUserId)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    public StatisticResponse getStatistics(String type) {
        Long currentUserId = authUtil.getCurrentUserId();

        TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
        List<Transaction> transactions =
                transactionRepository.findByWalletUserIdAndType(currentUserId, transactionType);

        if (transactions.isEmpty()) {
            return new StatisticResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        BigDecimal total = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> groupByCategory = transactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        Collectors.mapping(Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<CategoryStatistic> categories = groupByCategory.entrySet().stream()
                .map(e -> {
                    double percent = total.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().doubleValue() * 100.0 / total.doubleValue()
                            : 0.0;
                    return new CategoryStatistic(e.getKey(), e.getValue(), percent);
                })
                .toList();

        if (transactionType == TransactionType.EXPENSE) {
            return new StatisticResponse(total, BigDecimal.ZERO, BigDecimal.ZERO, categories);
        } else {
            return new StatisticResponse(BigDecimal.ZERO, total, BigDecimal.ZERO, categories);
        }
    }
}
