package com.soumen.kalpavriksha.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentTransaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    @Enumerated(EnumType.STRING)
    private PaymentStatus transactionStatus;
    private double amount;
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist()
    {
        createdAt = LocalDateTime.now();
    }

}

