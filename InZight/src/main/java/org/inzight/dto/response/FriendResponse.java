package org.inzight.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FriendResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long friendId;
    private String friendName; // 👈 thêm dòng này
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
