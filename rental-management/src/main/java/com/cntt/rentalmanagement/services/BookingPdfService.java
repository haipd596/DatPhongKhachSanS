package com.cntt.rentalmanagement.services;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.cntt.rentalmanagement.domain.models.Booking;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class BookingPdfService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateBookingDocument(Booking booking, String purpose) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("REX SAI GON HOTEL"));
            document.add(new Paragraph("Phiếu xác nhận: " + purpose));
            document.add(new Paragraph("Mã đặt phòng: #" + booking.getId()));
            document.add(new Paragraph("Khách hàng: " + booking.getUser().getFullName()));
            document.add(new Paragraph("Email: " + booking.getUser().getEmail()));
            document.add(new Paragraph("Phòng: " + booking.getRoom().getCode() + " - " + booking.getRoom().getRoomType().getName()));
            document.add(new Paragraph("Ngày nhận phòng: " + booking.getCheckInDate()));
            document.add(new Paragraph("Ngày trả phòng: " + booking.getCheckOutDate()));
            document.add(new Paragraph("Trạng thái: " + booking.getStatus().name()));
            document.add(new Paragraph("Tổng tiền: " + booking.getTotalAmount() + " VND"));
            document.add(new Paragraph("Ngày tạo: " + booking.getCreatedAt().format(DATE_TIME_FMT)));
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo tệp PDF", ex);
        }
    }
}
