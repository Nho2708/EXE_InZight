package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Wallet;
import org.inzight.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class WalletService {

    private final WalletRepository walletRepository;

    public List<Wallet> getWalletsByUser(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Wallet updateWallet(Long id, Wallet wallet) {
        Wallet existing = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        existing.setName(wallet.getName());
        existing.setBalance(wallet.getBalance());
        existing.setCurrency(wallet.getCurrency());
        return walletRepository.save(existing);
    }

    public void deleteWallet(Long id) {
        walletRepository.deleteById(id);
    }
}