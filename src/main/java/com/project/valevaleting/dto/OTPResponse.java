package com.project.valevaleting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class OTPResponse {
    private boolean isValid;
    private String phoneNumber;

    public static OTPResponse validResponse(String phoneNumber){
        return OTPResponse.builder()
                .isValid(true)
                .phoneNumber(phoneNumber)
                .build();
    }
}
