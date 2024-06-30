package com.project.valevaleting.dto;

import com.project.valevaleting.dto.response.PageDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingDetailsResponse {
    private long totalBookingToday;
    private long totalBookingThisWeek;
    private long totalBookingThisMonth;
    private double totalIncomeToday;
    private double totalIncomeThisWeek;
    private double totalIncomeThisMonth;
    private PageDto pageDto;
}
