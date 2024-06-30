package com.project.valevaleting.controller;


import com.project.valevaleting.dto.BookingDto;
import com.project.valevaleting.dto.ResponseDto;
import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.service.booking_service.BookingService;
import com.project.valevaleting.service.security.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Validated
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@CrossOrigin("*") // To be adjusted to prod frontend url
public class UserController {

    private final BookingService bookingService;
    private final AuthenticationService authenticationService;



    @PostMapping(value = "/book", produces = "application/json")
    public ResponseEntity<ResponseDto> bookAsUser(HttpServletRequest request, @RequestBody @Valid BookingRequest bookingRequest) {
        UserDto userDto = authenticationService.fetchUser(request);
        BookingDto response = bookingService.bookAsUser(userDto, bookingRequest);
        return new ResponseEntity<>(ResponseDto.wrapSuccessResult(response,"successful"), HttpStatus.OK);
    }

    @GetMapping(value = "/booking-history", produces = "application/json")
    public ResponseEntity<ResponseDto> fetchBookingHistory(HttpServletRequest request) {
        UserDto userDto = authenticationService.fetchUser(request);
        List<BookingDto> response = bookingService.fetchBookingHistory(userDto);
        return new ResponseEntity<>(ResponseDto.wrapSuccessResult(response,"successful"), HttpStatus.OK);
    }


}
