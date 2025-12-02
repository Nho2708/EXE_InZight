package org.inzight.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class ResendEmailService {

    private final WebClient webClient;

    public ResendEmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("RESEND_API_KEY"))
                .build();
    }

    public void sendOtpEmail(String toEmail, String otp) {
        webClient.post()
                .uri("/emails")
                .bodyValue(Map.of(
                        "from", "InZight@fintertaiment.fpt.edu.vn",
                        "to", toEmail,
                        "subject", "Mã OTP InZight",
                        "html", "<p>Mã OTP của bạn: <b>" + otp + "</b></p>"
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(r -> System.out.println("Email OTP gửi thành công tới " + toEmail))
                .doOnError(e -> e.printStackTrace())
                .subscribe();
    }
}
