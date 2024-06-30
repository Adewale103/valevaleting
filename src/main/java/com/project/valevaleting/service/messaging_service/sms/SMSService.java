package com.project.valevaleting.service.messaging_service.sms;

public interface SMSService {

    void sendOTP(String phoneNumber, String otp);
}
