package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;

import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.util.Random;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PayOS payOS;
    private final AuthUtil authUtil;



    public String createPayment(String plan) {

        Long amount = switch (plan) {
            case "VIP_1_MONTH" -> 100_000L;
            case "VIP_6_MONTH" -> 555_000L;
            case "VIP_12_MONTH" -> 999_000L;
            default -> throw new RuntimeException("Invalid plan");
        };

        Long userId = authUtil.getCurrentUserId();
        Long orderCode = userId * 1_000_000 + new Random().nextInt(999_999);

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description("Upgrade Premium: " + plan)
                .returnUrl("https://yourdomain.com/payment-success")
                .cancelUrl("https://yourdomain.com/payment-cancel")
                .expiredAt((System.currentTimeMillis() / 1000) + 900)
                .build();

        try {
            CreatePaymentLinkResponse response =
                    payOS.paymentRequests().create(request);

            return response.getCheckoutUrl();

        } catch (Exception e) {
            throw new RuntimeException("PayOS error: " + e.getMessage());
        }
    }
}

