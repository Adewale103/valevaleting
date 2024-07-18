package com.project.valevaleting.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.project.valevaleting.exception.GenericException;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Utils {
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    public static String generateRandom() {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = uuidToBytes(uuid);
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }

    public static String sanitizeRandom() {
        return sanitizeRandom(generateRandom());
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }


    public static long nairaToKobo(BigDecimal nairaAmount) {
        return (nairaAmount.multiply(BigDecimal.valueOf(100))).longValue(); // 100 Kobo = 1 Naira
    }

    public static double koboToNaira(double koboAmount) {
        return koboAmount / 100.0; // 1 Naira = 100 Kobo
    }

    public static String sanitizeRandom(String random) {
        char[] chars = random.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length);
        for (char aChar : chars) {
            if (Character.isLetterOrDigit(aChar)) {
                sb.append(aChar);
            }
        }
        return sb.toString().trim();
    }

    public static String generateRandomOTPNumber() {
        Instant instant = Instant.now();
        Random rnd = new Random();
        long timeStampMills = instant.getEpochSecond();
        long gettingFirstSixDigitFromTimeStamp = timeStampMills / 10000;
        long number = gettingFirstSixDigitFromTimeStamp + rnd.nextInt(999999);
        long generatedNumber = 0;
        if (number > 999999) {
            generatedNumber = number / 10;
            return String.format("%06d", generatedNumber);
        }
        return String.format("%06d", number);
    }

    public static String getDateAsString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    public static LocalDateTime convertToNigeriaTime(LocalDateTime dateTime) {
        ZoneId nigeriaZone = ZoneId.of("Africa/Lagos");
        ZonedDateTime nigeriaTime = dateTime.atZone(nigeriaZone);
        return nigeriaTime.toLocalDateTime();
    }

    // Method to return time as a string (in 12-hour format with AM/PM)
    public static String getTimeAsString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return dateTime.format(formatter);
    }

    public static String generateTicketNumber() {
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 10)
                .mapToObj(i -> characters.charAt(random.nextInt(characters.length())))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String convertDate(LocalDateTime dateTime){
       return dateTime.format(DateTimeFormatter.ofPattern("MMMM d yyyy"));
    }

    public static byte[] generateQRCode(String code, int width, int height)  {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BitMatrix bitMatrix = new MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception exception){
            throw new GenericException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



}
