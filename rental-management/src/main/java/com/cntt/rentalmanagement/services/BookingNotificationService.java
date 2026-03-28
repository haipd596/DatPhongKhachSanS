package com.cntt.rentalmanagement.services;

import org.springframework.stereotype.Service;

import com.cntt.rentalmanagement.domain.models.Booking;

@Service
public class BookingNotificationService {

    private final EmailService emailService;

    public BookingNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendHoldConfirmation(Booking booking) {
        emailService.send(
            booking.getUser().getEmail(),
            "Rex Sai Gon - Giữ phòng thành công",
            "Đơn đặt phòng #" + booking.getId() + " đã được giữ đến " + booking.getHoldExpiresAt()
        );
    }

    public void sendBookingConfirmed(Booking booking) {
        emailService.send(
            booking.getUser().getEmail(),
            "Rex Sai Gon - Đặt phòng thành công",
            "Đơn đặt phòng #" + booking.getId() + " đã thanh toán thành công. Tổng tiền: " + booking.getTotalAmount()
        );
    }

    public void sendBookingCancelled(Booking booking) {
        emailService.send(
            booking.getUser().getEmail(),
            "Rex Sai Gon - Hủy phòng thành công",
            "Đơn đặt phòng #" + booking.getId() + " đã được hủy."
        );
    }

    public void sendPasswordResetCode(String email, String code) {
        emailService.send(email, "Rex Sai Gon - Mã đổi mật khẩu", "Mã đổi mật khẩu của bạn là: " + code);
    }
}
