package com.project.valevaleting.repository;


import com.project.valevaleting.entities.Booking;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends CrudRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    boolean existsByEmailAndPaymentReference(String email, String paymentReference);

    Optional<Booking> findByPaymentReference(String reference);
    Optional<Booking> findByReference(String reference);
    List<Booking> findByEmailOrderByDateCreatedDesc(String email);
}