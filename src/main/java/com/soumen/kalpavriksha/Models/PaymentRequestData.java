package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class PaymentRequestData
{
    private String userId;
    private String email; // creator email
    private String pricePlan;
    private String teamId;
    private String amount;
    private String teamUniqueId;
    private String razorpay_payment_id;
    private String razorpay_order_id;
    private String razorpay_signature;
    private String paymentId;
    private String uniqueId;
    private String transactionId;
    private String billingCycle;

}
