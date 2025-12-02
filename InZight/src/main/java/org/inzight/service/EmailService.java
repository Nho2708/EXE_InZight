// FILE: src/main/java/org/inzight/service/EmailService.java

package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Sẽ tự động tiêm JavaMailSender vào constructor
public class EmailService {
//
//    // SỬA LỖI: Để Spring tự động tiêm bean JavaMailSender đã được cấu hình.
//    private final JavaMailSender mailSender;
//
//    @Value("${app.mail.sender}")
//    private String senderEmail;
//
//    /**
//     * Gửi mã OTP tới địa chỉ email người dùng.
//     * @param toEmail Địa chỉ email nhận.
//     * @param otpCode Mã OTP được sinh ra.
//     */
//    public void sendOtpEmail(String toEmail, String otpCode) {
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        if (toEmail == null || toEmail.trim().isEmpty()) {
//            System.err.println("Lỗi: Không thể gửi email OTP vì địa chỉ email nhận bị trống.");
//            return;
//        }
//
//        message.setFrom(senderEmail);
//        message.setTo(toEmail);
//        message.setSubject("Mã Xác Thực OTP của Ứng dụng InZight");
//
//        String text = String.format(
//                "Mã xác thực (OTP) của bạn là: %s\n\n" +
//                        "Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.",
//                otpCode
//        );
//        message.setText(text);
//
//        try {
//            mailSender.send(message); // Đã sửa lỗi: Gọi trực tiếp phương thức send()
//            System.out.println("Email OTP đã được gửi thành công đến: " + toEmail);
//        } catch (Exception e) {
//            // Lỗi này giờ thường là lỗi xác thực hoặc kết nối (email/mật khẩu sai)
//            System.err.println("LỖI GỬI EMAIL OTP tới " + toEmail + ": " + e.getMessage());
//            e.printStackTrace(); // Nên in stack trace để debug kết nối SMTP
//        }
//    }
}