package com.project.valevaleting.service.booking_service;

import com.google.common.util.concurrent.AtomicDouble;
import com.project.valevaleting.dto.BookingDetailsResponse;
import com.project.valevaleting.dto.BookingDto;
import com.project.valevaleting.dto.UserDto;
import com.project.valevaleting.dto.request.BookingRequest;
import com.project.valevaleting.dto.response.PageDto;
import com.project.valevaleting.entities.Booking;
import com.project.valevaleting.exception.GenericException;
import com.project.valevaleting.repository.BookingRepository;
import com.project.valevaleting.service.cloudinary.CloudinaryService;
import com.project.valevaleting.service.messaging_service.email.EmailService;
import com.project.valevaleting.specifications.BookingSpecs;
import com.project.valevaleting.utils.BeanUtilHelper;
import com.project.valevaleting.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.project.valevaleting.utils.Constants.*;
import static com.project.valevaleting.utils.EnumUtils.generateReference;
import static com.project.valevaleting.utils.Utils.generateRandomOTPNumber;


@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final EmailService emailService;
    private final BookingRepository bookingRepository;
    private final CloudinaryService cloudinaryService;
    private final String qrCodeUrl;
    private final RestTemplate restTemplate;


    public BookingServiceImpl(EmailService emailService,
                              RestTemplateBuilder restTemplateBuilder,
                              CloudinaryService cloudinaryService,
                              @Value(value = "${qr.code.url}") String qrCodeUrl,
                             BookingRepository bookingRepository) {
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
        this.restTemplate = restTemplateBuilder.build();
        this.bookingRepository = bookingRepository;
        this.qrCodeUrl = qrCodeUrl;
    }


    @Override
    public BookingDto bookAsGuest(BookingRequest bookingRequest) {
        return createBookingFrom(bookingRequest);
    }


    @Override
    public BookingDto bookAsUser(UserDto userDto, BookingRequest bookingRequest) {
        return createBookingFrom(bookingRequest);
    }

    @Override
    public List<BookingDto> fetchBookingHistory(UserDto userDto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Booking> bookings = bookingRepository.findByEmailOrderByDateCreatedDesc(userDto.getEmail());
        return bookings.stream().map(BookingDto::map).sorted(Comparator.comparing(
                bookingDto -> {
                    String createdDateString = bookingDto.getPaymentDate();
                    return createdDateString.isEmpty() ? LocalDate.MIN : LocalDate.parse(createdDateString, dateFormatter);
                },
                Comparator.reverseOrder()
        )).toList();
    }

    @Override
    public BookingDetailsResponse viewAllBookingDetails(BookingSpecs bookingSpecs) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        BookingDetailsResponse bookingDetailsResponse  = new BookingDetailsResponse();
        Page<Booking> page = bookingRepository.findAll(bookingSpecs, bookingSpecs.getPageable());
        AtomicLong totalBookingToday = new AtomicLong(0);
        AtomicLong totalBookingThisWeek = new AtomicLong(0);
        AtomicLong totalBookingThisMonth = new AtomicLong(0);
        AtomicDouble totalIncomeToday = new AtomicDouble(0.0);
        AtomicDouble totalIncomeThisWeek = new AtomicDouble(0.0);
        AtomicDouble totalIncomeThisMonth = new AtomicDouble(0.0);

        analyseBookingDetails(page, totalBookingToday, totalIncomeToday, totalBookingThisWeek, totalIncomeThisWeek, totalBookingThisMonth, totalIncomeThisMonth);
        List<BookingDto> bookingDtoList = page.stream().map(BookingDto::map).sorted(Comparator.comparing(
                bookingDto -> {
                    String createdDateString = bookingDto.getPaymentDate();
                    return createdDateString.isEmpty() ? LocalDate.MIN : LocalDate.parse(createdDateString, dateFormatter);
                },
                Comparator.reverseOrder()
        )).toList();

        return buildBookingDetailsResponse(bookingDetailsResponse, totalBookingToday, totalBookingThisWeek, totalBookingThisMonth, totalIncomeToday, totalIncomeThisWeek, totalIncomeThisMonth, page, bookingDtoList);
        
    }

    @Override
    public String verifyBooking(String reference) {
        Booking booking = bookingRepository.findByReference(reference).orElseThrow(()-> new GenericException(BOOKING_NOT_FOUND,HttpStatus.BAD_REQUEST));
        if(booking.isValidated()){
            throw new GenericException(ALREADY_VALIDATED,HttpStatus.BAD_REQUEST);
        }
        booking.setValidated(true);
        bookingRepository.save(booking);
        return VALIDATED;
    }

    private void analyseBookingDetails(Page<Booking> page, AtomicLong totalBookingToday, AtomicDouble totalIncomeToday, AtomicLong totalBookingThisWeek, AtomicDouble totalIncomeThisWeek, AtomicLong totalBookingThisMonth, AtomicDouble totalIncomeThisMonth) {
        page.stream().forEach(booking -> {
            LocalDate bookingDate = booking.getDateCreated().toLocalDate();
            LocalDate today = LocalDate.now();

            if (bookingDate.isEqual(today)) {
                totalBookingToday.incrementAndGet();
                totalIncomeToday.addAndGet(Double.parseDouble(booking.getAmount()));
            }
            if (bookingDate.isAfter(today.minusDays(7)) && bookingDate.isBefore(today.plusDays(1))) {
                totalBookingThisWeek.incrementAndGet();
                totalIncomeThisWeek.addAndGet(Double.parseDouble(booking.getAmount()));
            }
            if (bookingDate.getMonth().getValue() == today.getMonthValue() && bookingDate.getYear() == today.getYear()) {
                totalBookingThisMonth.incrementAndGet();
                totalIncomeThisMonth.addAndGet(Double.parseDouble(booking.getAmount()));
            }

        });
    }

    private BookingDetailsResponse buildBookingDetailsResponse(BookingDetailsResponse bookingDetailsResponse, AtomicLong totalBookingToday, AtomicLong totalBookingThisWeek, AtomicLong totalBookingThisMonth, AtomicDouble totalIncomeToday, AtomicDouble totalIncomeThisWeek, AtomicDouble totalIncomeThisMonth, Page<Booking> page, List<BookingDto> bookingDtoList) {
        bookingDetailsResponse.setTotalBookingToday(totalBookingToday.get());
        bookingDetailsResponse.setTotalBookingThisWeek(totalBookingThisWeek.get());
        bookingDetailsResponse.setTotalBookingThisMonth(totalBookingThisMonth.get());
        bookingDetailsResponse.setTotalIncomeToday(totalIncomeToday.get());
        bookingDetailsResponse.setTotalIncomeThisWeek(totalIncomeThisWeek.get());
        bookingDetailsResponse.setTotalIncomeThisMonth(totalIncomeThisMonth.get());
        bookingDetailsResponse.setPageDto(PageDto.build(page, bookingDtoList));
        return bookingDetailsResponse;
    }


    private BookingDto createBookingFrom(BookingRequest bookingRequest) {
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        BeanUtilHelper.copyPropertiesIgnoreNull(bookingRequest, booking);
        booking.setReference(generateReference());
        booking.setPaymentReference(generateReference());
        booking = bookingRepository.save(booking);
        byte[] qr = Utils.generateQRCode(booking.getReference(),400,400);
        String url = cloudinaryService.uploadFile(qr,"qrcode");
        sendBookingEmail(booking, qr);
        BeanUtilHelper.copyPropertiesIgnoreNull(booking,bookingDto);
        bookingDto.setPaymentDate(booking.getDateCreated().toLocalDate().toString());
        bookingDto.setQrCodeUrl(url);
        return bookingDto;
    }

    private void sendBookingEmail(Booking booking, byte[] qr) {
        try {
            emailService.sendBookingDetails(booking, qr);
        }
        catch (Exception e){
            throw new GenericException("Error occurred while sending email. Please contact support", HttpStatus.BAD_REQUEST);
        }
    }

}
