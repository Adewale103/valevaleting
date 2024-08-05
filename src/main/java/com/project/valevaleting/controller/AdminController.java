package com.project.valevaleting.controller;


import com.project.valevaleting.dto.BookingDetailsResponse;
import com.project.valevaleting.dto.BookingDto;
import com.project.valevaleting.dto.ResponseDto;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.service.booking_service.BookingService;
import com.project.valevaleting.specifications.BookingSpecs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Validated
@PreAuthorize("hasAnyRole('ADMIN')")
@CrossOrigin("*") // To be adjusted to prod frontend url
public class AdminController {

    private final BookingService bookingService;

    @GetMapping(value = "/view-bookings", produces = "application/json")
    public ResponseEntity<ResponseDto> viewAllBookingDetails(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {

        BookingSpecs bookingSpecs = new BookingSpecs(query);
        bookingSpecs.setPage(page);
        bookingSpecs.setSize(size);
//        bookingSpecs.setSort(Sort.by(Sort.Order.desc("dateCreated")));
        BookingDetailsResponse response = bookingService.viewAllBookingDetails(bookingSpecs);
        return new ResponseEntity<>(ResponseDto.wrapSuccessResult(response,"successful"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/verify/{reference}", produces = "application/json")
    public ResponseEntity<ResponseDto> verifyBooking(@PathVariable String reference) {
        String response = bookingService.verifyBooking(reference);
        return new ResponseEntity<>(ResponseDto.wrapSuccessResult(response,"successful"), HttpStatus.OK);
    }

}
