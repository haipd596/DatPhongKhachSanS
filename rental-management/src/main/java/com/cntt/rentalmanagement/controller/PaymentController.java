package com.cntt.rentalmanagement.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cntt.rentalmanagement.domain.payload.request.MockPaymentRequest;
import com.cntt.rentalmanagement.domain.payload.response.BookingResponse;
import com.cntt.rentalmanagement.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/mock-success")
    public ResponseEntity<BookingResponse> mockSuccess(@Validated @RequestBody MockPaymentRequest request, Principal principal) {
        return ResponseEntity.ok(paymentService.mockSuccess(request.bookingId(), principal.getName()));
    }
}
