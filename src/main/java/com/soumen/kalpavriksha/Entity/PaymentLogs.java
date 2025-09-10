package com.soumen.kalpavriksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PaymentLogs
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String paymentMethod;
    private String cardLast4;
    private String cardId;
    private String cardType;
    private String cardName;
    private String cardIssuer;
    private String cardNetwork;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String eventType;
    private String eventTime;
    private String details;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        createdAt = LocalDateTime.now();
    }
}
