package com.project.valevaleting.repository;


import com.project.valevaleting.entities.Booking;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface BookingRepository extends CrudRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    boolean existsByEmailAndPaymentReference(String email, String paymentReference);

    List<Booking> findByPaymentReference(String reference);
    List<Booking> findByReference(String reference);
    List<Booking> findByEmail(String email);
}