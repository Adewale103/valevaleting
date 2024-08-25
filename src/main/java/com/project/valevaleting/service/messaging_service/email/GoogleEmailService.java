package com.project.valevaleting.service.messaging_service.email;

import com.project.valevaleting.entities.Booking;
import com.project.valevaleting.entities.User;
import com.project.valevaleting.utils.EmailUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleEmailService implements EmailService{
    private final EmailUtils emailUtils;
    @Override
    public void sendWelcomeEmail(String toEmail, String emailContent) {

    }

    @Override
    public void sendOTPEmail(User user) throws MessagingException {
        emailUtils.sendOTPEmail(user.getFirstname(), user.getOtp(), user.getEmail());
    }

    @Override
    public void sendBookingDetails(Booking booking, byte[] qrCode) throws MessagingException {
        emailUtils.sendBookingDetails(booking, qrCode);
    }


    @Override
    public void sendPaymentComplaintEmail(String email, BigDecimal amountPaid, Double amount) throws MessagingException {
      emailUtils.generatePaymentDiscrepancyEmail(email, amountPaid.doubleValue(), amount);
    }
}
