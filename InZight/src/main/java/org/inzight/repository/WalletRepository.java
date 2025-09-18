package org.inzight.repository;

import org.inzight.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserId(Long userId);
}