package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.entity.Wallet;
import org.inzight.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // ✅ yêu cầu JWT
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<List<Wallet>> getWallets(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(walletService.getWalletsByUser(getUserId(user)));
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet,
                                               @AuthenticationPrincipal UserDetails user) {
        wallet.setId(getUserId(user));
        return ResponseEntity.ok(walletService.createWallet(wallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable Long id, @RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.updateWallet(id, wallet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: map username -> userId (tạm fix cứng để test)
    private Long getUserId(UserDetails user) {
        if (user.getUsername().equals("alice")) return 1L;
        if (user.getUsername().equals("bob")) return 2L;
        throw new RuntimeException("User không tồn tại trong demo mapping!");
    }
}