package com.project.valevaleting.service.security;


import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.AuthenticationRequest;
import com.project.valevaleting.dto.request.RegisterRequest;
import com.project.valevaleting.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    UserDto fetchUser(HttpServletRequest httpServletRequest);
}
