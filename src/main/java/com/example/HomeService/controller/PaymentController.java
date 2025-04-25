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
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//@RestController
//public class PaymentController {
//
//    @Value("${stripe.api.key}")
//    private String stripeApiKey;
//
//    private final OrdersRepository ordersRepository;
//    private final PaymentRepository paymentRepository;
//
//    public PaymentController(OrdersRepository ordersRepository, PaymentRepository paymentRepository) {
//        this.ordersRepository = ordersRepository;
//        this.paymentRepository = paymentRepository;
//    }
//
//    @GetMapping("/payment/success")
//    public ResponseEntity<Void> paymentSuccess(@RequestParam("session_id") String sessionId) {
//        try {
//            RequestOptions requestOptions = RequestOptions.builder().setApiKey(stripeApiKey).build();
//            Session session = Session.retrieve(sessionId, requestOptions);
//
//            if ("paid".equals(session.getPaymentStatus())) {
//                Payment payment = paymentRepository.findBySessionId(sessionId);
//
//                if (payment != null) {
//                    payment.setPaymentStatus("PAID");
//                    payment.setPaidAt(LocalDateTime.now());
//                    payment.setStripePaymentIntentId(session.getPaymentIntent());
//                    paymentRepository.save(payment);
//
//                    Orders order = payment.getOrder();
//                    order.setPaymentStatus("PAID");
//                    ordersRepository.save(order);
//
//                    //  Redirect to frontend success page
//                    return ResponseEntity.status(HttpStatus.FOUND)
//                            .location(URI.create("http://localhost:5173/success"))
//                            .build();
//                } else {
//                    // Still redirect to frontend (could also use a /failure page here)
//                    return ResponseEntity.status(HttpStatus.FOUND)
//                            .location(URI.create("http://localhost:5173/failure"))
//                            .build();
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.FOUND)
//                        .location(URI.create("http://localhost:5173/failure"))
//                        .build();
//            }
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(URI.create("http://localhost:5173/failure"))
//                    .build();
//        }
//    }
//
//
//    @GetMapping("/payment/cancel")
//    public ResponseEntity<String> handlePaymentFailure() {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
//    }
//}

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
    public ResponseEntity<Void> paymentSuccess(@RequestParam("token") String token) {
        Orders order = ordersRepository.findBySuccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or expired token"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found"));

        try {
            RequestOptions requestOptions = RequestOptions.builder().setApiKey(stripeApiKey).build();
            Session session = Session.retrieve(payment.getSessionId(), requestOptions);

            if ("paid".equals(session.getPaymentStatus())) {
                if (!"PAID".equals(payment.getPaymentStatus())) {
                    payment.setPaymentStatus("PAID");
                    payment.setPaidAt(LocalDateTime.now());
                    payment.setStripePaymentIntentId(session.getPaymentIntent());
                    paymentRepository.save(payment);

                    order.setPaymentStatus("PAID");
                    order.setSuccessToken(null); // Invalidate token after use
                    ordersRepository.save(order);
                }

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("http://localhost:5173/success"))
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("http://localhost:5173/failure"))
                        .build();
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:5173/failure"))
                    .build();
        }
    }

    @GetMapping("/payment/cancel")
    public ResponseEntity<String> handlePaymentFailure() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
    }
}
