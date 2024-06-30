package com.project.valevaleting.utils;

import com.project.valevaleting.entities.Booking;
import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Objects;


@RequiredArgsConstructor
@Component
public class EmailUtils {
    private final JavaMailSender mailSender;

    public void generatePaymentDiscrepancyEmail(String email, double initiatedAmount, Double amount) throws MessagingException {
        String subject = "Vale Valenting Services: Payment Discrepancy Email";
        String messageContent = generatePaymentDiscrepancyEmailContent(email,initiatedAmount,amount);
        sendEmail(subject,email,messageContent, null);
    }

    //Todo Remove hardcoded event details and make it dynamic
    public void sendBookingDetails(Booking booking, byte[] qrCode) throws MessagingException {
        String subject = "Vale Valenting Services: Your booking was successful";

        String messageContent = generateBookingEmailContent();
        sendEmail(subject,booking.getEmail(),messageContent, qrCode);
    }

    private void sendEmail(String subject, String receiverEmail, String messageContent, byte[] qrCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom("valevaletingservices@gmail.com");
        message.setRecipients(Message.RecipientType.TO, receiverEmail);
        message.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent(messageContent, "text/html; charset=utf-8");
        multipart.addBodyPart(textBodyPart);

       if(Objects.nonNull(qrCode)) {
           MimeBodyPart qrCodeBodyPart = new MimeBodyPart();
           qrCodeBodyPart.setHeader("Content-ID", "<qrcode>");
           qrCodeBodyPart.setFileName("qrcode.png");
           qrCodeBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(qrCode, "image/png")));
           qrCodeBodyPart.setDisposition(Part.ATTACHMENT);
           multipart.addBodyPart(qrCodeBodyPart);
           message.setContent(multipart);
       }
        mailSender.send(message);
    }


    //Todo Move to Email Template table after migration for flexibility and change. Another option is to pass via properties file



    public String generatePaymentDiscrepancyEmailContent(String userEmail, double initiatedAmount, double actualAmount) {
        String cssStyles = "<style>\n" +
                "    body { font-family: Arial, sans-serif; background-color: #f2f2f2; }\n" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 2px solid #d9534f; }\n" +
                "    h1 { color: #d9534f; font-size: 24px; margin-bottom: 20px; }\n" +
                "    p { color: #000000; font-size: 16px; line-height: 1.5; }\n" +
                "    a { color: #d9534f; text-decoration: none; }\n" +
                "    a:hover { text-decoration: underline; }\n" +
                "</style>";

        // HTML content for the email
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                cssStyles +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1 style=\"border-bottom: 2px solid #d9534f; padding-bottom: 10px;\">Attention Admin, " + "!</h1>\n" +
                "        <p>We have detected a discrepancy in the payment amount for the following user:</p>\n" +
                "        <p><strong>Email:</strong> " + userEmail + "</p>\n" +
                "        <p><strong>Initiated Amount:</strong> ₦" + String.format("%.2f", initiatedAmount) + "</p>\n" +
                "        <p><strong>Actual Amount:</strong> ₦" + String.format("%.2f", actualAmount) + "</p>\n" +
                "        <p>Please review and take the necessary action to resolve this issue.</p>\n" +
                "        <p>If you have any questions, please contact support immediately.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }


    //Todo Move to properties file
    private String generateBookingEmailContent() {
        return """
        <!DOCTYPE html>
                           <html lang="en">
                           
                           <head>
                               <meta charset="UTF-8">
                               <meta http-equiv="X-UA-Compatible" content="IE=edge">
                               <meta name="viewport" content="width=device-width, initial-scale=1.0">
                               <style>
                                   body {
                                       background-color: #ffffff;
                                       font-family: Arial, sans-serif;
                                       color: #333;
                                       margin: 0;
                                       padding: 0;
                                   }
                                   .container {
                                       max-width: 600px;
                                       margin: auto;
                                       padding: 20px;
                                       text-align: center;
                                       background-color: #ffffff;
                                       border-radius: 5px;
                                       box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                                   }
                                   .header {
                                       padding: 20px;
                                   }
                                   .content {
                                       text-align: left;
                                       color: #333;
                                   }
                                   .order-summary {
                                       margin-top: 40px;
                                   }
                                   .service-details {
                                       background-color: #4CAF50;
                                       color: white;
                                       padding: 20px;
                                       margin-top: 25px;
                                       margin-bottom: 25px;
                                       border-radius: 5px;
                                   }
                                   .footer {
                                       color: #5a5a5a;
                                   }
                                   .footer a {
                                       color: #4CAF50;
                                       text-decoration: none;
                                   }
                                   .qr-code {
                                       margin-top: 20px;
                                       text-align: center;
                                   }
                               </style>
                           </head>
                           
                           <body>
                                   <div class="content">
                                       <p style="font-weight: bold; font-size: 16px;">Hello Valued Customer,</p>
                                       <p>Thank you for choosing our services. Your booking for a car wash service has been successfully confirmed.</p>
                           
                                       <div class="order-summary">
                                           <p style="font-weight: bold; font-size: 18px;">Order Summary</p>
                                           <p style="font-weight: bold;">Booking Reference: <span style="color: #4CAF50;">kbjbekbeq</span></p>
                                       </div>
                           
                                       <div class="service-details">
                                           <p style="font-weight: 600; font-size: 20px; line-height: 24px;">Car Wash Service Details</p>
                                           <p>Service Date: 27th June, 2024</p>
                                           <p>Time: 10am</p>
                                           <p>Location: Vale Valeting Services Center</p>
                                       </div>
                                       <p>Find attached the QR code generated for the booking.</p>
                                       <div class="footer">
                                           <p style="margin-top: 30px; margin-bottom: 45px;">For more questions and info contact us at <a href="mailto:valevaletingservices@gmail.com">valevaletingservices@gmail.com</a></p>
                                           <p style="color: #555; text-align: center;">Thank you for your business,<br>Vale Valeting Services</p>
                                       </div>
                                   </div>
                               </div>
                           </body>
                           
                           </html>
                           
    """;
    }



}
