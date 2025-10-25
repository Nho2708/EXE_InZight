package org.inzight.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private Long userId;
    private String username;
    private List<CommentResponse> comments;
    private int likeCount;
    private int shareCount;
    private boolean liked;

    private Instant createdAt;
    private Instant updatedAt;
}
