package com.project.valevaleting.service.messaging_service.email;

import com.project.valevaleting.entities.Booking;
import com.project.valevaleting.entities.User;
import jakarta.mail.MessagingException;

import java.math.BigDecimal;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String emailContent);

    void sendBookingDetails(Booking booking, byte[] qrCode) throws MessagingException;

    void sendPaymentComplaintEmail(String email, BigDecimal amountPaid, Double amount) throws MessagingException;
}
