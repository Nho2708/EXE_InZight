package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.PaymentResponse;
import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;

import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.*;

import java.time.LocalDateTime;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PayOS payOS;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;



    public PaymentResponse createPayment(String plan) {

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

            return new PaymentResponse(response.getCheckoutUrl(), orderCode);

        } catch (Exception e) {
            throw new RuntimeException("PayOS error: " + e.getMessage());
        }
    }

    /**
     * Verify payment và kiểm tra xem user đã được update rank chưa
     * 
     * Logic:
     * 1. Giải mã userId từ orderCode
     * 2. Kiểm tra xem user đã có rank PREMIUM và còn hạn chưa
     * 3. Nếu có -> payment đã được webhook xử lý thành công
     * 4. Nếu chưa -> có thể webhook chưa xử lý xong hoặc payment chưa thành công
     * 
     * Lưu ý: Webhook sẽ tự động update rank khi payment thành công.
     * Method này chỉ kiểm tra kết quả của webhook, không gọi PayOS API.
     * Android app nên retry sau vài giây nếu lần đầu trả về false.
     */
    public boolean verifyAndUpgradePayment(Long orderCode) {
        try {
            System.out.println("\n==============================");
            System.out.println("🔍 VERIFY PAYMENT");
            System.out.println("==============================");
            System.out.println("👉 OrderCode = " + orderCode);

            // 1) Giải mã userId từ orderCode (giống logic trong WebhookController)
            Long userId = orderCode / 1_000_000;
            System.out.println("👉 Decoded userId = " + userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        System.out.println("❌ User not found: " + userId);
                        return new RuntimeException("User not found");
                    });

            System.out.println("👉 Current rank = " + user.getRank());
            System.out.println("👉 Rank expired at = " + user.getRankExpiredAt());

            // 2) Kiểm tra xem user đã được update rank chưa
            // Nếu đã là PREMIUM và còn hạn -> payment đã được webhook xử lý thành công
            boolean isPremium = "PREMIUM".equals(user.getRank());
            boolean isActive = user.getRankExpiredAt() != null 
                    && user.getRankExpiredAt().isAfter(LocalDateTime.now());

            if (isPremium && isActive) {
                System.out.println("✅ Payment verified: User đã có rank PREMIUM và còn hạn");
                System.out.println("🏆 Rank = " + user.getRank());
                System.out.println("⏳ Rank Expired = " + user.getRankExpiredAt());
                return true; // Đã được update rồi
            }

            // 3) Nếu chưa update, có thể:
            // - Webhook chưa xử lý xong (cần đợi thêm)
            // - Payment chưa thành công
            // - Webhook bị lỗi
            System.out.println("⚠️ Payment chưa được verify:");
            System.out.println("   - Rank = " + user.getRank() + " (expected: PREMIUM)");
            System.out.println("   - Rank expired = " + user.getRankExpiredAt());
            System.out.println("   - Có thể webhook chưa xử lý xong, vui lòng retry sau vài giây");
            
            return false;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR VERIFY PAYMENT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

