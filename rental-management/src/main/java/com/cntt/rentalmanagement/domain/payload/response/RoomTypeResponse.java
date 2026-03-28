package com.cntt.rentalmanagement.domain.payload.response;

import java.math.BigDecimal;

public record RoomTypeResponse(
    Long id,
    String name,
    BigDecimal basePrice,
    Integer maxGuests,
    String description
) {
}
