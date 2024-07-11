package com.project.valevaleting.dto.request;

import com.project.valevaleting.enums.CarType;
import com.project.valevaleting.enums.ServiceType;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BookingRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String amount;
    private String additionalInfo;
    private String vehicleRegistrationNumber;
    private ServiceType serviceType;
    private CarType carType;
    private String date;
    private String time;
}
