package com.example.HomeService.controller;

import com.stripe.model.checkout.Session;
import com.stripe.exception.StripeException;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.HomeService.repository.OrdersRepository;
import com.example.HomeService.repository.PaymentRepository;
import com.example.HomeService.model.Payment;
import com.example.HomeService.model.Orders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final OrdersRepository ordersRepository;
    private final PaymentRepository paymentRepository;

    public PaymentController(OrdersRepository ordersRepository, PaymentRepository paymentRepository) {
        this.ordersRepository = ordersRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/payment/success")
    public ResponseEntity<Map<String, Object>> paymentSuccess(@RequestParam("session_id") String sessionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            RequestOptions requestOptions = RequestOptions.builder().setApiKey(stripeApiKey).build();
            Session session = Session.retrieve(sessionId, requestOptions);

            if ("paid".equals(session.getPaymentStatus())) {
                Payment payment = paymentRepository.findBySessionId(sessionId);

                if (payment != null) {
                    payment.setPaymentStatus("PAID");
                    payment.setPaidAt(LocalDateTime.now());
                    payment.setStripePaymentIntentId(session.getPaymentIntent());
                    paymentRepository.save(payment);

                    Orders order = payment.getOrder();
                    order.setPaymentStatus("PAID");
                    ordersRepository.save(order);

                    response.put("success", true);
                    response.put("message", "Payment successful and order updated.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Payment session not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.put("success", false);
                response.put("message", "Payment not completed.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (StripeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Stripe error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/payment/cancel")
    public ResponseEntity<String> handlePaymentFailure() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
    }
}
