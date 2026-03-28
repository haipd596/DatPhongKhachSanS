package com.cntt.rentalmanagement.domain.payload.response;

public record AvailabilitySummaryResponse(
    Long roomTypeId,
    String roomTypeName,
    long totalRooms,
    long reservedRooms,
    long availableRooms
) {
}
