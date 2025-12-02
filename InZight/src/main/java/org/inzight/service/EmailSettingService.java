package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailSettingService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.resend.com")
            .build();

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    public void send(String to, String subject, String body) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromEmail);
        payload.put("to", to);
        payload.put("subject", subject);
        payload.put("html", body);

        webClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + apiKey)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(err -> {
                    System.err.println("Gửi email thất bại: " + err.getMessage());
                })
                .subscribe(response -> {
                    System.out.println("Email gửi thành công: " + response);
                });
    }
}
