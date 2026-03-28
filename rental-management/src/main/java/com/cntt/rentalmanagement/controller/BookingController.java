package com.cntt.rentalmanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cntt.rentalmanagement.domain.payload.request.HoldBookingRequest;
import com.cntt.rentalmanagement.domain.payload.response.BookingResponse;
import com.cntt.rentalmanagement.services.BookingPdfService;
import com.cntt.rentalmanagement.services.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingPdfService bookingPdfService;

    public BookingController(BookingService bookingService, BookingPdfService bookingPdfService) {
        this.bookingService = bookingService;
        this.bookingPdfService = bookingPdfService;
    }

    @PostMapping("/hold")
    public ResponseEntity<BookingResponse> hold(@Validated @RequestBody HoldBookingRequest request, Principal principal) {
        return ResponseEntity.ok(bookingService.holdRoom(principal.getName(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> myBookings(Principal principal) {
        return ResponseEntity.ok(bookingService.myBookings(principal.getName()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable("id") Long id, Principal principal) {
        return ResponseEntity.ok(bookingService.cancel(id, principal.getName()));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable("id") Long id,
                                            @RequestParam(name = "purpose", defaultValue = "Đặt phòng") String purpose,
                                            Principal principal) {
        byte[] content = bookingPdfService.generateBookingDocument(bookingService.getOwnedBooking(id, principal.getName()), purpose);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(content);
    }
}
