package com.project.valevaleting.service.booking_service;


import com.project.valevaleting.dto.*;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.specifications.BookingSpecs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    BookingDto bookAsGuest(BookingRequest bookingRequest);

    BookingDto bookAsUser(UserDto userDto, BookingRequest bookingRequest);

    List<BookingDto> fetchBookingHistory(UserDto userDto);

    BookingDetailsResponse viewAllBookingDetails(BookingSpecs bookingSpecs);

    String verifyBooking(String reference);
}
