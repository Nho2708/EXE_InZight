package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Transaction;
import org.inzight.repository.TransactionRepository;
import org.inzight.repository.WalletRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public Transaction createTransaction(Transaction transaction, UserDetails user) {
        // kiểm tra wallet thuộc về user
        var wallet = walletRepository.findById(transaction.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        // TODO: check wallet.getUserId() == currentUserId
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction transaction, UserDetails user) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        // TODO: check quyền sở hữu
        existing.setAmount(transaction.getAmount());
        existing.setNote(transaction.getNote());
        existing.setId(transaction.getId());
        existing.setType(transaction.getType());
        existing.setTransactionDate(transaction.getTransactionDate());
        return transactionRepository.save(existing);
    }

    public List<Transaction> getTransactionsByWallet(Long walletId, UserDetails user) {
        // TODO: check quyền sở hữu
        return transactionRepository.findByWalletId(walletId);
    }

    public Transaction getTransaction(Long id, UserDetails user) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void deleteTransaction(Long id, UserDetails user) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        // TODO: check quyền sở hữu
        transactionRepository.delete(existing);
    }
}