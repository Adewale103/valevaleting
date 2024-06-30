package com.project.valevaleting.controller;



import com.project.valevaleting.dto.BookingDto;
import com.project.valevaleting.dto.ResponseDto;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.service.booking_service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guests")
@CrossOrigin("*") // To be adjusted to prod frontend url
public class GuestController {

    private final BookingService bookingService;


    @PostMapping(value = "/book", produces = "application/json")
    public ResponseEntity<ResponseDto> bookAsGuest(@RequestBody @Valid BookingRequest bookingRequest) {
        BookingDto response = bookingService.bookAsGuest(bookingRequest);
        return new ResponseEntity<>(ResponseDto.wrapSuccessResult(response,"successful"), HttpStatus.OK);
    }

}
