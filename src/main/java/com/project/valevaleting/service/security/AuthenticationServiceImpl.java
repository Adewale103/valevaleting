package com.project.valevaleting.service.security;


import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.AuthenticationRequest;
import com.project.valevaleting.dto.request.RegisterRequest;
import com.project.valevaleting.dto.response.AuthenticationResponse;
import com.project.valevaleting.entities.User;
import com.project.valevaleting.enums.TokenType;
import com.project.valevaleting.exception.GenericException;
import com.project.valevaleting.repository.UserRepository;
import com.project.valevaleting.utils.BeanUtilHelper;
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

import static com.project.valevaleting.utils.Constants.USER_ALREADY_EXIST;
import static com.project.valevaleting.utils.EnumUtils.generateReference;


@Service @Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

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


}
