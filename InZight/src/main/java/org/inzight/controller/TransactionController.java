package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.entity.Transaction;
import org.inzight.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ✅ Thêm mới transaction (chi tiêu hoặc thu nhập)
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction,
                                                         @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction, user));
    }

    // ✅ Sửa transaction
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id,
                                                         @RequestBody Transaction transaction,
                                                         @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction, user));
    }

    // ✅ Xem tất cả transaction theo ví
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactionsByWallet(@PathVariable Long walletId,
                                                                     @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(transactionService.getTransactionsByWallet(walletId, user));
    }

    // ✅ Xem chi tiết 1 transaction
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(transactionService.getTransaction(id, user));
    }

    // ✅ Xoá transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails user) {
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }
}
