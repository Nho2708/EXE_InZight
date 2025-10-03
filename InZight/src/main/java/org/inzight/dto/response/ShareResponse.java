package org.inzight.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShareResponse {
    private Long userId;
    private String username;
}
