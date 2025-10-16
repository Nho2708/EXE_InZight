package org.inzight.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReplyResponse {
    private Long id;
    private String username;
    private String avatarUrl;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
