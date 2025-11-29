package org.inzight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.inzight.entity.User;
import vn.payos.PayOS;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;


import org.inzight.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pay/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PayOS payOS;
    private final UserRepository userRepository;

    @PostMapping
    public Object webhook(@RequestBody Map<String, Object> body) {

        System.out.println("\n==============================");
        System.out.println("🔥 WEBHOOK HIT");
        System.out.println("==============================");
        System.out.println("📥 RAW BODY = " + body);
        try {
            // 1) Verify chữ ký (bắt buộc)
            payOS.webhooks().verify(body);

            System.out.println("✅ Signature verified!");

            ObjectMapper mapper = new ObjectMapper();
            Webhook webhook = mapper.convertValue(body, Webhook.class);
            WebhookData data = webhook.getData();

            String status = webhook.getCode();  // "PAID"
            Long orderCode = data.getOrderCode();
            Long amount = data.getAmount();



            if (!"00".equals(status)) {
                return Map.of("success", true, "message", "Not paid");
            }

            // 3) Giải mã userId từ orderCode
            Long userId = orderCode / 1_000_000;

            User user = userRepository.findById(userId)
                    .orElseThrow();

            // 4) Xác định số ngày Premium
            int days = switch (amount.intValue()) {
                case 100_000 -> 30;   // 1 tháng
                case 555_000 -> 180;  // 6 tháng
                case 999_000 -> 365;  // 12 tháng
                default -> 0;
            };

            System.out.println("🔍 Parsed Webhook = " + webhook);
            System.out.println("🔍 Data = " + data);
            System.out.println("👉 Status (code) = " + status);
            System.out.println("👉 OrderCode = " + orderCode);
            System.out.println("👉 Amount = " + amount);
            System.out.println("👉 Decode userId = " + userId);

            // 5) Gia hạn hoặc kích hoạt Premium
            if (days > 0) {
                if (user.getRankExpiredAt() != null && user.getRankExpiredAt().isAfter(LocalDateTime.now())) {
                    user.setRankExpiredAt(user.getRankExpiredAt().plusDays(days));
                } else {
                    user.setRank("PREMIUM");
                    user.setRankExpiredAt(LocalDateTime.now().plusDays(days));
                }
                userRepository.save(user);
            }

            System.out.println("🎉 USER UPDATED SUCCESSFULLY: " + user.getUsername());
            System.out.println("🏆 Rank = " + user.getRank());
            System.out.println("⏳ Rank Expired = " + user.getRankExpiredAt());

            return Map.of("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ ERROR WEBHOOK: " + e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}
