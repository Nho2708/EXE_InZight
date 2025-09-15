package org.inzight.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long userid;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}