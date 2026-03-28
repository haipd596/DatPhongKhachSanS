package com.cntt.rentalmanagement.domain.payload.response;

public record AuthResponse(
    String accessToken,
    String email,
    String fullName,
    String role,
    String vipLevel
) {
}
