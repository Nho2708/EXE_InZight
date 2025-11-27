package org.inzight.dto.request;

// Đảm bảo Record này là public
public record RegisterRequest(
        String username,
        String email,
        String fullName,
        String dateOfBirth,
        String gender,
        String password
) {}
