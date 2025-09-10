package com.soumen.kalpavriksha.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double paymentAmount;
    private String pricePlan;
    private String orderId; // order id generated internally for further reference
    private String razorpayOrderId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private LocalDateTime paymentTime;

    @ManyToOne
    @JoinColumn(name = "payment_initiator_id")
    private User user;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    @ToString.Exclude
    PaymentLogs paymentLogs;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    @ToString.Exclude
    PaymentTransaction paymentTransaction;

}
