package com.project.valevaleting.specifications;


import com.project.valevaleting.entities.Booking;
import lombok.ToString;

@ToString
public class BookingSpecs extends QueryToCriteria<Booking> {
    public BookingSpecs(String query) {
        super(query);
    }
}
