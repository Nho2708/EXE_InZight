package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.TransactionRequest;
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

    // Lấy transaction của user theo income hoặc expense
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserAndType(type));
    }
    // get all transaction cua user
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getTransactions(){
        return ResponseEntity.ok(transactionService.getTransactions());
    }

    // ✅ Thêm mới transaction (chi tiêu hoặc thu nhập)
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(transaction);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id,
                                                         @RequestBody TransactionRequest request) {
        Transaction updated  = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Transaction> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

//    // ✅ Sửa transaction
//    @PutMapping("/{id}")
//    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id,
//                                                         @RequestBody Transaction transaction,
//                                                         @AuthenticationPrincipal UserDetails user) {
//        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction, user));
//    }
//
//    // ✅ Xem tất cả transaction theo ví
//    @GetMapping("/wallet/{walletId}")
//    public ResponseEntity<List<Transaction>> getTransactionsByWallet(@PathVariable Long walletId,
//                                                                     @AuthenticationPrincipal UserDetails user) {
//        return ResponseEntity.ok(transactionService.getTransactionsByWallet(walletId, user));
//    }
//
//    // ✅ Xem chi tiết 1 transaction
//    @GetMapping("/{id}")
//    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id,
//                                                      @AuthenticationPrincipal UserDetails user) {
//        return ResponseEntity.ok(transactionService.getTransaction(id, user));
//    }
//
//    // ✅ Xoá transaction
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id,
//                                                  @AuthenticationPrincipal UserDetails user) {
//        transactionService.deleteTransaction(id, user);
//        return ResponseEntity.noContent().build();
//    }
}
