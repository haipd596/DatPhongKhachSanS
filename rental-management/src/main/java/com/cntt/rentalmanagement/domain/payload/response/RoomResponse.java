package com.cntt.rentalmanagement.domain.payload.response;

import java.math.BigDecimal;

public record RoomResponse(
    Long id,
    String code,
    Integer floorNumber,
    String status,
    Long roomTypeId,
    String roomTypeName,
    BigDecimal basePrice,
    Integer maxGuests
) {
}
