package com.project.valevaleting.service.security;


import com.project.valevaleting.dto.OTP;
import com.project.valevaleting.dto.OTPResponse;
import com.project.valevaleting.dto.ResetPassword;
import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.AuthenticationRequest;
import com.project.valevaleting.dto.request.RegisterRequest;
import com.project.valevaleting.dto.response.AuthenticationResponse;
import com.project.valevaleting.entities.User;
import com.project.valevaleting.enums.TokenType;
import com.project.valevaleting.exception.GenericException;
import com.project.valevaleting.repository.UserRepository;
import com.project.valevaleting.service.messaging_service.email.EmailService;
import com.project.valevaleting.utils.BeanUtilHelper;
import com.project.valevaleting.utils.EmailUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.project.valevaleting.utils.Constants.*;
import static com.project.valevaleting.utils.EnumUtils.generateReference;
import static com.project.valevaleting.utils.Utils.generateRandomOTPNumber;


@Service @Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GenericException(USER_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .reference(generateReference())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        user = userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .firstName(user.getFirstname())
                .roleName(String.valueOf(request.getRole()))
                .email(user.getEmail())
                .id(user.getReference())
                .refreshToken(refreshToken.getToken())
                .roles(roles)
                .tokenType(TokenType.BEARER.name())
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .roleName(String.valueOf(user.getRole()))
                .roles(roles)
                .email(user.getEmail())
                .id(user.getReference())
                .refreshToken(refreshToken.getToken())
                .tokenType( TokenType.BEARER.name())
                .build();
    }

    @Override
    public UserDto fetchUser(HttpServletRequest httpServletRequest) {
        String email = jwtService.extractUserName(httpServletRequest.getHeader("Authorization").substring(7));
        UserDto userDto = new UserDto();
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()-> new GenericException("User not found", HttpStatus.BAD_REQUEST));
        BeanUtilHelper.copyPropertiesIgnoreNull(user, userDto);
        return userDto;
    }

    public String sendOTP(OTP request) throws MessagingException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        String otp = generateRandomOTPNumber();
        user.setOtp(otp);
        userRepository.save(user);
        emailService.sendOTPEmail(user);
        return OTP_SENT;
    }

    @Override
    public String resetPassword(ResetPassword request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        if(Objects.isNull(user.getOtp()) || !request.getOtp().equalsIgnoreCase(user.getOtp())){
         throw new GenericException(INVALID_OTP, HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return PASSWORD_RESET_SUCCESSFUL;
    }


}
