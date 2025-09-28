package org.inzight.repository;

import org.inzight.entity.Transaction;
import org.inzight.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    List<Transaction> findByWalletUserIdAndType(Long userId, TransactionType type);
    List<Transaction> findByWalletUserId(Long userId);
}