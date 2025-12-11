package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.PaymentResponse;
import org.inzight.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> create(@RequestParam String plan) {
        PaymentResponse response = paymentService.createPayment(plan);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestParam Long orderCode) {
        boolean success = paymentService.verifyAndUpgradePayment(orderCode);
        if (success) {
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Payment verified and rank updated"));
        } else {
            return ResponseEntity.ok(java.util.Map.of("success", false, "message", "Payment not found or not paid"));
        }
    }
}
