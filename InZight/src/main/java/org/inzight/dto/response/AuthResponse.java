package org.inzight.dto.response;

import org.inzight.enums.RoleName;

public record AuthResponse(
        String token,
        String username,
        RoleName role,   // 👉 THÊM ROLE
        String rank
) {}
