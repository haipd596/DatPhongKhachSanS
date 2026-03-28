package com.cntt.rentalmanagement.domain.payload.response;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    String fullName,
    Integer rating,
    String comment,
    LocalDateTime createdAt
) {
}
