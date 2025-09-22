package org.inzight.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.dto.request.TransactionRequest;
import org.inzight.entity.Category;
import org.inzight.entity.Transaction;
import org.inzight.entity.User;
import org.inzight.entity.Wallet;
import org.inzight.enums.TransactionType;
import org.inzight.repository.CategoryRepository;
import org.inzight.repository.UserRepository;
import org.inzight.repository.TransactionRepository;
import org.inzight.repository.WalletRepository;
import org.inzight.security.AuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final AuthUtil authUtil;

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
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

            // update balance
            if (transaction.getType() == TransactionType.INCOME) {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }

            walletRepository.save(wallet);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error creating transaction", e);
            throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
        }
    }
    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionRequest request) {
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

            // update category
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
            return transactionRepository.save(transaction);
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
}


//    public Transaction createTransaction(Transaction transaction, UserDetails user) {
//        // kiểm tra wallet thuộc về user
//        var wallet = walletRepository.findById(transaction.getId())
//                .orElseThrow(() -> new RuntimeException("Wallet not found"));
//        // TODO: check wallet.getUserId() == currentUserId
//        return transactionRepository.save(transaction);
//    }
//
//    public Transaction updateTransaction(Long id, Transaction transaction, UserDetails user) {
//        Transaction existing = transactionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//        // TODO: check quyền sở hữu
//        existing.setAmount(transaction.getAmount());
//        existing.setNote(transaction.getNote());
//        existing.setId(transaction.getId());
//        existing.setType(transaction.getType());
//        existing.setTransactionDate(transaction.getTransactionDate());
//        return transactionRepository.save(existing);
//    }
//
//    public List<Transaction> getTransactionsByWallet(Long walletId, UserDetails user) {
//        // TODO: check quyền sở hữu
//        return transactionRepository.findByWalletId(walletId);
//    }
//
//    public Transaction getTransaction(Long id, UserDetails user) {
//        return transactionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//    }
//
//    public void deleteTransaction(Long id, UserDetails user) {
//        Transaction existing = transactionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//        // TODO: check quyền sở hữu
//        transactionRepository.delete(existing);
//    }
