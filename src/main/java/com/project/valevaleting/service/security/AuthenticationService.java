package com.project.valevaleting.service.security;


import com.project.valevaleting.dto.OTP;
import com.project.valevaleting.dto.OTPResponse;
import com.project.valevaleting.dto.ResetPassword;
import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.AuthenticationRequest;
import com.project.valevaleting.dto.request.RegisterRequest;
import com.project.valevaleting.dto.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    UserDto fetchUser(HttpServletRequest httpServletRequest);

    String sendOTP(OTP otp) throws MessagingException;

    String resetPassword(@Valid ResetPassword request);
}
