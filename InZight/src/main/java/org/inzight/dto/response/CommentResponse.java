package org.inzight.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
}
