package org.inzight.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}
