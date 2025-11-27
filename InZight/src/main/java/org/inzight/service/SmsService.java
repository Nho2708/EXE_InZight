package org.inzight.service;

import com.twilio.Twilio; // Import Twilio
import com.twilio.rest.api.v2010.account.Message; // Import Message
import com.twilio.type.PhoneNumber; // Import PhoneNumber
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    // Tiêm các giá trị từ application.yml
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    /**
     * Gửi mã OTP tới số điện thoại.
     * @param toPhoneNumber Số điện thoại nhận (Định dạng E.164, ví dụ: +84XXXXXXXXX).
     * @param otpCode Mã OTP.
     */
    public void sendOtpSms(String toPhoneNumber, String otpCode) {
        if (toPhoneNumber == null || toPhoneNumber.trim().isEmpty()) {
            System.err.println("Lỗi: Không thể gửi SMS OTP vì số điện thoại bị trống.");
            return;
        }

        // 1. Khởi tạo Twilio Client
        Twilio.init(accountSid, authToken);

        // Chuẩn bị nội dung tin nhắn
        String smsBody = String.format("Ma xac thuc (OTP) cua ban la: %s. Ma nay se het han sau 5 phut. Vui long khong chia se ma nay voi bat ky ai.", otpCode);

        try {
            // 2. Gọi API gửi SMS
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),   // Số điện thoại nhận (Định dạng E.164)
                    new PhoneNumber(twilioPhoneNumber), // Số điện thoại Twilio của bạn
                    smsBody
            ).create();

            System.out.println("SMS OTP đã được gửi thành công. SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("LỖI GỬI SMS OTP tới " + toPhoneNumber + ": " + e.getMessage());
            // Log lỗi chi tiết
        }
    }
}