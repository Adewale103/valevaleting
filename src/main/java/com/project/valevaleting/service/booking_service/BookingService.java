package com.project.valevaleting.service.booking_service;


import com.project.valevaleting.dto.BookingDetailsResponse;
import com.project.valevaleting.dto.BookingDto;
import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.specifications.BookingSpecs;

import java.util.List;

public interface BookingService {

    BookingDto bookAsGuest(BookingRequest bookingRequest);

    BookingDto bookAsUser(UserDto userDto, BookingRequest bookingRequest);

    List<BookingDto> fetchBookingHistory(UserDto userDto);

    BookingDetailsResponse viewAllBookingDetails(BookingSpecs bookingSpecs);
}
