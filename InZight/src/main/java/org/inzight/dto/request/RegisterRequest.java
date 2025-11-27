package org.inzight.dto.request;

import org.inzight.enums.RoleName;

// Đảm bảo Record này là public
public record RegisterRequest(
        String username,
        String email,
        String fullName,
        String dateOfBirth,
        String gender,
        String password,
        RoleName role
) {

}
