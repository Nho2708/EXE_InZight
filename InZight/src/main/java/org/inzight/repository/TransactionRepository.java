package org.inzight.repository;

import org.inzight.entity.Transaction;
import org.inzight.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    List<Transaction> findByWalletUserIdAndType(Long userId, TransactionType type);
    List<Transaction> findByWalletUserId(Long userId);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.user.id = :userId " +
            "AND t.type = :type " +
            "AND FUNCTION('MONTH', t.transactionDate) = :month " +
            "AND FUNCTION('YEAR', t.transactionDate) = :year")
    List<Transaction> findByUserAndTypeAndMonth(@Param("userId") Long userId,
                                                @Param("type") TransactionType type,
                                                @Param("month") int month,
                                                @Param("year") int year);

}