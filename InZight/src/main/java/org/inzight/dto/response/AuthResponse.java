package org.inzight.dto.response;

public record AuthResponse(
        String token,
        String username,
        String email,
        String avatarUrl,
        String fullName
) {}
