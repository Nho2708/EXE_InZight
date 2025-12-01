package org.inzight.dto.response;
import org.inzight.enums.RoleName;

public record RegisterResponse(
        String token,
        String username,
        String email,
        String avatarUrl,
        String fullName,
        RoleName role,   // 👉 THÊM ROLE
        String rank
)
{ }
