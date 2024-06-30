package com.project.valevaleting.entities;

import com.project.valevaleting.enums.CarType;
import com.project.valevaleting.enums.Role;
import com.project.valevaleting.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
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

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private CarType carType;
    private String date;
    private String time;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreated;

}
