package com.soumen.kalpavriksha.Service;


import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.soumen.kalpavriksha.Config.RazorpayConfig;
import com.soumen.kalpavriksha.Entity.*;
import com.soumen.kalpavriksha.Repository.PaymentLogsRepository;
import com.soumen.kalpavriksha.Repository.PaymentRepository;
import com.soumen.kalpavriksha.Repository.PaymentTransactionRepository;
import com.soumen.kalpavriksha.Repository.UserRepository;
import com.soumen.kalpavriksha.Utills.RazorpaySignature;
import com.soumen.kalpavriksha.Utills.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentLogsRepository paymentLogsRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    private double getPlanPrice(String plan, String billingCycle)
    {
        if(plan.equalsIgnoreCase("plus"))
        {
            if(billingCycle.equalsIgnoreCase("monthly"))
            {
                return 299.0;
            }
            else
            {
                return 2999.0;
            }
        }
        else
        {
            if(billingCycle.equalsIgnoreCase("monthly"))
            {
                return 499.0;
            }
            else
            {
                return 4999.0;
            }
        }
    }

    public Map<String , Object> handlePaymentService(String userId, String pricePlan, String billingCycle)
    {
        try{
            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

            if(userOptional.isEmpty())
            {
                return Response.error("User not found");
            }

            User user = userOptional.get();

            double amount = getPlanPrice(pricePlan.toLowerCase().trim(), billingCycle.toLowerCase().trim());

            System.out.println("pricePlan : " + pricePlan + " price : " + amount);

            JSONObject orderResponseJson = null;
            try{
                orderResponseJson = capturePayment(userId, amount);
            } catch (RazorpayException e) {
                e.printStackTrace();
                return Response.error(e.getMessage() + " " + "Something went wrong");
            }

            if(orderResponseJson == null)
            {
                return Response.error("Something went wrong");
            }

            System.out.println("Order Response : " + orderResponseJson);

            String orderId = generateOrderId();
            System.out.println("Order Id: " + orderId);

            String razorpayOrderId = orderResponseJson.getString("id");
            System.out.println("Razorpay Order Id: " + razorpayOrderId);

            LocalDateTime paymentInitiationTime = LocalDateTime.now();

            com.soumen.kalpavriksha.Entity.Payment payment = new com.soumen.kalpavriksha.Entity.Payment();
            payment.setPaymentAmount(amount);
            payment.setPricePlan(pricePlan);
            payment.setOrderId(orderId);
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setUser(user);
            payment.setPaymentTime(paymentInitiationTime);
            payment.setPaymentStatus(PaymentStatus.PENDING);

             com.soumen.kalpavriksha.Entity.Payment paymentOutput = paymentRepository.save(payment);

             PaymentTransaction paymentTransaction = new PaymentTransaction();

             paymentTransaction.setPayment(paymentOutput);
             paymentTransaction.setAmount(amount);
             paymentTransaction.setRazorpayOrderId(razorpayOrderId);
             paymentTransaction.setTransactionStatus(PaymentStatus.PENDING);

             PaymentTransaction paymentOutputTransaction = paymentTransactionRepository.save(paymentTransaction);

            System.out.println("paymentOutputTransaction : " + paymentOutputTransaction);

            Map<String , Object> paymentResponse = new HashMap<>();
            paymentResponse.put("uniqueId", userId);
            paymentResponse.put("orderId", orderId);
            paymentResponse.put("razorpayOrderId", razorpayOrderId);
            paymentResponse.put("amount", amount * 100);
            paymentResponse.put("pricePlan", pricePlan);
            paymentResponse.put("paymentInitiationTime", paymentInitiationTime);
            paymentResponse.put("userId", userId);
            paymentResponse.put("key", razorpayConfig.getKeyId());
            paymentResponse.put("name", user.getName());
            paymentResponse.put("email", user.getEmail());
            paymentResponse.put("paymentId", paymentOutput.getId());
            paymentResponse.put("transactionId", paymentOutputTransaction.getId());

            System.out.println("paymentResponse : " + paymentResponse);

            return Response.success("Payment Initiated Successfully", paymentResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    private JSONObject capturePayment(String userId, Double amount) throws RazorpayException
    {
        // JSONObject orderResponse = new JSONObject();
        System.out.println("Capturing Payment");
        Order orderResponse = orderCreation(amount, userId);
        System.out.println("Order : " + orderResponse);

        JSONObject orderResponseJson = new JSONObject(orderResponse.toString());
        String orderId = orderResponseJson.getString("id");
        System.out.println("Order Id: " + orderId);
        orderResponseJson.put("key", razorpayConfig.getKeyId());

        // Need to save orderId in database

        System.out.println("orderResponseJson : " + orderResponseJson);

        if (orderResponseJson.has("error"))
        {
            return null;
        }
        else
        {
            return orderResponseJson;
        }
    }

    private Order orderCreation(Double amount, String userId) throws RazorpayException
    {
        RazorpayClient razorpay = new RazorpayClient(razorpayConfig.getKeyId(), razorpayConfig.getKeySecret());

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100.0); // Amount is in currency subunits.
        orderRequest.put("currency","INR");
        orderRequest.put("receipt", "receipt#1");

        JSONObject notes = new JSONObject();
        notes.put("userId", userId);
        notes.put("razorpayId", razorpayConfig.getKeyId());

        orderRequest.put("notes", notes);

        // Order order = instance.orders.create(orderRequest);
        return razorpay.orders.create(orderRequest);
    }

    private String generateOrderId()
    {
        int length = 8;
        String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            int index = secureRandom.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    public Map<String, Object> verifyPayment(
            String userId,
            String transactionId,
            String paymentId,
            int amount,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature
    )
    {
        System.out.println("Inside verifyPayment method in service layer");

        System.out.println("paymentId : " + paymentId);
        System.out.println("amount : " + amount);
        System.out.println("razorpayPaymentId : " + razorpayPaymentId);
        System.out.println("razorpayOrderId : " + razorpayOrderId);
        System.out.println("razorpaySignature : " + razorpaySignature);

        try{
            String bodyData = razorpayOrderId + "|" + razorpayPaymentId;
            String generated_signature = RazorpaySignature.calculateHMac(razorpayConfig.getKeySecret(), bodyData);

            System.out.println("generated_signature : " + generated_signature);
            System.out.println("razorpay_signature : " + razorpaySignature);

            Map<String , Object> output;

            if(generated_signature.equals(razorpaySignature))
            {
                System.out.println("Signature Verified");

                // perform db operation

                Optional <PaymentTransaction> paymentTransaction = paymentTransactionRepository.findById(Integer.parseInt(transactionId));

                if(paymentTransaction.isEmpty())
                {
                    return Response.error("Transaction not found");
                }

                PaymentTransaction transaction = paymentTransaction.get();

                transaction.setRazorpayPaymentId(razorpayPaymentId);

                LocalDateTime time = LocalDateTime.now();

                Optional<com.soumen.kalpavriksha.Entity.Payment> paymentOptional = paymentRepository.findById(Integer.parseInt(paymentId));

                if(paymentOptional.isEmpty())
                {
                    return Response.error("Payment not found");
                }

                com.soumen.kalpavriksha.Entity.Payment payment = paymentOptional.get();

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setPaymentTime(time);

                Map<String , Object> resp = new HashMap<>();

                resp.put("razorpayPaymentId", razorpayPaymentId);
                resp.put("paymentId", paymentId);

                output = getPaymentMethod(razorpayPaymentId, amount);

                if(!(boolean) output.get("success"))
                {
                    return Response.error(output.get("message"));
                }

                JSONObject paymentDetails = (JSONObject) output.get("data");

                JSONObject notes = (JSONObject) paymentDetails.get("notes");


                String paymentMethod = paymentDetails.get("method").toString();

                JSONObject card = (JSONObject) paymentDetails.get("card");

                String cardLast4Digits = card.get("last4").toString();
                String cardId = card.get("id").toString();
                String cardType = card.get("type").toString();
                String cardName = card.get("name").toString();
                String cardIssuer = card.get("issuer").toString();
                String cardNetwork = card.get("network").toString();
                String status = paymentDetails.get("status").toString();


                System.out.println("paymentMethod : " + paymentMethod);
                System.out.println("cardLast4Digits : " + cardLast4Digits);
                System.out.println("cardId : " + cardId);
                System.out.println("cardType : " + cardType);
                System.out.println("cardName : " + cardName);
                System.out.println("cardIssuer : " + cardIssuer);
                System.out.println("cardNetwork : " + cardNetwork);
                System.out.println("status : " + status);

                Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

                if(userOptional.isEmpty())
                {
                    return Response.error("User not found");
                }

                User user = userOptional.get();

                PaymentLogs paymentLogs = new PaymentLogs();

                paymentLogs.setPayment(payment);
                paymentLogs.setUser(user);
                paymentLogs.setPaymentMethod(paymentMethod);
                paymentLogs.setCardLast4(cardLast4Digits);
                paymentLogs.setCardId(cardId);
                paymentLogs.setCardType(cardType);
                paymentLogs.setCardName(cardName);
                paymentLogs.setCardIssuer(cardIssuer);
                paymentLogs.setCardNetwork(cardNetwork);
                paymentLogs.setStatus(PaymentStatus.SUCCESS);
                paymentLogs.setEventType("complete");
                paymentLogs.setEventTime(time.toString());
                paymentLogs.setCreatedAt(time);

                paymentLogsRepository.save(paymentLogs);

                int credits = 0;
                String pricePlan = payment.getPricePlan();

                if(pricePlan.equalsIgnoreCase("plus"))
                {
                    credits = 30;
                }
                else if(pricePlan.equalsIgnoreCase("pro"))
                {
                    credits = 40;
                }

                user.setCreditPoints(credits);
                userRepository.save(user);

                return Response.success("Signature Verified", resp);
            }
            else
            {
                System.out.println("Signature Not Verified");
                return Response.error("Signature Not Verified");
            }

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    private Map<String , Object> getPaymentMethod(String paymentId, int amount)
    {
        try{
            RazorpayClient razorpay = new RazorpayClient(razorpayConfig.getKeyId(), razorpayConfig.getKeySecret());

            Payment payment = razorpay.payments.fetch(paymentId);

            JSONObject paymentDetails = payment.toJson();

            System.out.println("paymentDetails : " + paymentDetails);
            System.out.println("paymentDetails : " + paymentDetails.toString());

            System.out.println("payment inside getPaymentMethod details: " + payment);

            return Response.success("Payment details fetched successfully", paymentDetails);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
}

