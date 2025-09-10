package com.soumen.kalpavriksha.Controllers;


import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Models.PaymentRequestData;
import com.soumen.kalpavriksha.Service.PaymentServiceImpl;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.CustomHTTPCode;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController
{
    @Autowired
    private PaymentServiceImpl service;

    @PostMapping("/initiate-payment")
    public ResponseEntity<Map<String , Object>> initiatePayment(@RequestBody PaymentRequestData paymentRequestData, Authentication authentication)
    {
        System.out.println("Inside handle Payment ....");

        System.out.println("paymentRequestData inside initiate payment : " + paymentRequestData);

        String pricePlan = paymentRequestData.getPricePlan();
        String billingCycle = paymentRequestData.getBillingCycle();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();

        if(Common.isNullOrEmpty(pricePlan) || Common.isNullOrEmpty(billingCycle))
        {
            return new ResponseEntity<>(Response.error("Required fields are missing"), HttpStatus.BAD_REQUEST);
        }

        if(Common.isNullOrEmpty(userId))
        {
            return new ResponseEntity<>(Response.error("Login Required to subscribe"), HttpStatus.UNAUTHORIZED);
        }

        if(pricePlan.equalsIgnoreCase("free"))
        {
            return new ResponseEntity<>(Response.error("By Default you are subscribed to free plan"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object > output = service.handlePaymentService(userId, pricePlan, billingCycle);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String , Object>> verifyPayment(@RequestBody PaymentRequestData paymentRequestData, Authentication authentication)
    {
        System.out.println("Inside verify Payment ....");

        System.out.println("paymentRequestData inside verify payment : " + paymentRequestData);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();

        String paymentId = paymentRequestData.getPaymentId();
        String razorpay_payment_id = paymentRequestData.getRazorpay_payment_id();
        String razorpay_order_id = paymentRequestData.getRazorpay_order_id();
        String razorpay_signature = paymentRequestData.getRazorpay_signature();
        String amount = paymentRequestData.getAmount();
        String transactionId = paymentRequestData.getTransactionId();

        if(Common.isNullOrEmpty(razorpay_payment_id) || Common.isNullOrEmpty(razorpay_order_id) || Common.isNullOrEmpty(razorpay_signature))
        {
            return new ResponseEntity<>(Response.error("Required fields are missing"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = service.verifyPayment(userId, transactionId, paymentId,Integer.parseInt(amount), razorpay_payment_id, razorpay_order_id, razorpay_signature);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

}

