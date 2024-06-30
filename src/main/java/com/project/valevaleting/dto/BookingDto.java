package com.project.valevaleting.dto;

import com.project.valevaleting.entities.Booking;
import com.project.valevaleting.enums.CarType;
import com.project.valevaleting.enums.ServiceType;
import com.project.valevaleting.utils.BeanUtilHelper;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BookingDto {
    private String email;
    private String reference;
    private String userReference;
    private String phoneNumber;
    private String paymentDate;
    private String amount;
    private boolean validated;
    private String paymentReference;
    private String additionalInfo;
    private String vehicleRegistrationNumber;
    private ServiceType serviceType;
    private CarType carType;
    private String date;
    private String time;

    public static BookingDto map(Booking booking){
        BookingDto bookingDto = new BookingDto();
        BeanUtilHelper.copyPropertiesIgnoreNull(booking, bookingDto);
        return  bookingDto;
    }
}
