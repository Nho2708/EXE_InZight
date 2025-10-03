package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.WalletRequest;
import org.inzight.dto.response.WalletResponse;
import org.inzight.entity.Wallet;
import org.inzight.mapper.WalletMapper;
import org.inzight.repository.UserRepository;
import org.inzight.service.UserService;
import org.inzight.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> getWallets(@AuthenticationPrincipal UserDetails user) {
        Long userId = getUserId(user);
        List<WalletResponse> wallets = walletService.getWalletsByUser(userId)
                .stream()
                .map(walletMapper::toResponse)
                .toList();
        return ResponseEntity.ok(wallets);
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody WalletRequest request,
                                                       @AuthenticationPrincipal UserDetails user) {
        Long userId = getUserId(user);
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        Wallet saved = walletService.createWallet(wallet);
        return ResponseEntity.ok(walletMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> updateWallet(@PathVariable Long id,
                                                       @RequestBody WalletRequest request) {
        Wallet updated = walletService.updateWallet(id, request, walletMapper);
        return ResponseEntity.ok(walletMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(UserDetails user) {
        return userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
