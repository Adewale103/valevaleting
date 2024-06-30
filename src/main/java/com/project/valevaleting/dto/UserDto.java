package com.project.valevaleting.dto;

import com.project.valevaleting.enums.CarType;
import com.project.valevaleting.enums.ServiceType;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}
