package com.soumen.kalpavriksha.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public String getKeyId() {
        return razorpayKeyId;
    }

    public String getKeySecret() {
        return razorpayKeySecret;
    }
}


