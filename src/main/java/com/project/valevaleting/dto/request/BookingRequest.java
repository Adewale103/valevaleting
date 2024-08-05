package com.project.valevaleting.dto.request;

import com.project.valevaleting.enums.CarType;
import com.project.valevaleting.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BookingRequest {
    private String firstName;
    private String lastName;
    @NotBlank
    private String email;
    private String phoneNumber;
    @NotBlank
    private String amount;
    private String additionalInfo;
    @NotBlank
    private String vehicleRegistrationNumber;
    private ServiceType serviceType;
    private CarType carType;
    private String date;
    private String time;
}
