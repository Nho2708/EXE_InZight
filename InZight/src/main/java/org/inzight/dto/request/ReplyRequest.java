package org.inzight.dto.request;

import lombok.Data;

@Data
public class ReplyRequest {
    private Long commentId;
    private String content;
}
