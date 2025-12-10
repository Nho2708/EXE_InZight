package org.inzight.service.Ai;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Gọi Gemini (Google Generative Language API) để sinh trả lời AI.
 * Cần cấu hình gemini.api.key (ENV hoặc application.yml). Không hard-code key trong code.
 */
@Service
public class GeminiAiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:}")
    private String apiKey;

    public GeminiAiService() {
        this.restTemplate = new RestTemplate();
    }

    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=%s";

    // System prompt về project InZight
    private static final String SYSTEM_PROMPT = """
        Bạn là Finbot, trợ lý AI thông minh của ứng dụng InZight - một ứng dụng quản lý tài chính cá nhân.
        
        **Về InZight:**
        - InZight là ứng dụng giúp người dùng quản lý chi tiêu và thu nhập hàng ngày
        - Người dùng có thể ghi lại các giao dịch (transaction) như chi tiêu (expense) và thu nhập (income)
        - Ứng dụng có các tính năng: Multi-Goal Planning, Optimizer, Scenario Analysis, Retirement Calculator
        - Người dùng có thể quản lý ví (wallet), danh mục (category), và xem lịch sử giao dịch
        
        **Nhiệm vụ của bạn:**
        - Trả lời các câu hỏi liên quan đến quản lý tài chính, chi tiêu, thu nhập, và các tính năng của InZight
        - Khi người dùng nhập thông tin giao dịch (ví dụ: "trà sữa 40k", "tiền điện 300k"), hệ thống đã tự động phát hiện và hiển thị card giao dịch. Bạn KHÔNG cần nhắc lại hoặc xác nhận thông tin đó.
        - Chỉ trả lời khi người dùng hỏi câu hỏi cụ thể hoặc cần tư vấn
        - Tư vấn về tài chính cá nhân, tiết kiệm, đầu tư
        - Nếu người dùng hỏi về chủ đề không liên quan đến tài chính/InZight, hãy nhẹ nhàng chuyển hướng về chủ đề tài chính
        
        **Cách trả lời:**
        - Ngắn gọn, thân thiện, tự nhiên
        - Sử dụng tiếng Việt
        - Đưa ra lời khuyên thực tế về quản lý tài chính
        - KHÔNG sử dụng các câu như "Mình đã ghi nhận", "Đã phát hiện giao dịch", "Bạn muốn thêm chi tiết nào khác không" vì hệ thống đã tự động xử lý
        - Nếu người dùng chỉ nhập giao dịch mà không có câu hỏi, hãy im lặng (không trả lời gì cả)
        """;

    public Optional<String> generateReply(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key is missing; skip AI call");
            return Optional.empty();
        }
        try {
            String url = String.format(URL, apiKey);
            log.debug("Calling Gemini API: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Kết hợp system prompt với user prompt
            String fullPrompt = SYSTEM_PROMPT + "\n\nNgười dùng: " + (userPrompt != null ? userPrompt : "");
            GeminiRequest request = GeminiRequest.of(fullPrompt);
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            log.debug("Sending request to Gemini (user prompt: {})", userPrompt);
            ResponseEntity<GeminiResponse> resp = restTemplate.exchange(
                    url, HttpMethod.POST, entity, GeminiResponse.class);

            log.debug("Gemini response status: {}", resp.getStatusCode());
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Optional<String> text = extractText(resp.getBody());
                log.debug("Gemini reply extracted: {}", text.orElse("empty"));
                return text;
            } else {
                log.warn("Gemini API returned non-2xx status: {}", resp.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Gemini call failed: {}", ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    private Optional<String> extractText(GeminiResponse body) {
        if (body == null || body.getCandidates() == null || body.getCandidates().isEmpty()) return Optional.empty();
        GeminiResponse.Candidate c = body.getCandidates().get(0);
        if (c.getContent() == null || c.getContent().getParts() == null || c.getContent().getParts().isEmpty()) return Optional.empty();
        return Optional.ofNullable(c.getContent().getParts().get(0).getText());
    }

    // ===== DTO =====
    @Data
    public static class GeminiRequest {
        private List<Content> contents;

        public static GeminiRequest of(String text) {
            GeminiRequest req = new GeminiRequest();
            Content c = new Content();
            Part p = new Part();
            p.setText(text == null ? "" : text);
            c.setParts(Collections.singletonList(p));
            req.setContents(Collections.singletonList(c));
            return req;
        }

        @Data
        public static class Content {
            private List<Part> parts;
        }

        @Data
        public static class Part {
            private String text;
        }
    }

    @Data
    public static class GeminiResponse {
        private List<Candidate> candidates;

        @Data
        public static class Candidate {
            private Content content;
        }

        @Data
        public static class Content {
            private List<Part> parts;
        }

        @Data
        public static class Part {
            private String text;
        }
    }
}

