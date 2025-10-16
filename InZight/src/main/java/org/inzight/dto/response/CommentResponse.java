package org.inzight.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private int likeCount;
    private boolean liked;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
