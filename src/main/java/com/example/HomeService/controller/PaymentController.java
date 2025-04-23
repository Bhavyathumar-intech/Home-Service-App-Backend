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
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        try {
            // Set your API key for Stripe
            RequestOptions requestOptions = RequestOptions.builder().setApiKey(stripeApiKey).build();

            // Retrieve the session from Stripe using the session_id
            Session session = Session.retrieve(sessionId, requestOptions);

            // Check the payment status of the session
            if ("paid".equals(session.getPaymentStatus())) {
                // Retrieve the corresponding payment using the session id
                Payment payment = paymentRepository.findBySessionId(sessionId);
                if (payment != null) {
                    // Update the payment status to "PAID" in the Payment entity
                    payment.setPaymentStatus("PAID");
                    payment.setPaidAt(LocalDateTime.now()); // Mark the payment time
                    paymentRepository.save(payment);

                    // Retrieve the related order
                    Orders order = payment.getOrder();
                    order.setPaymentStatus("PAID");
                    ordersRepository.save(order);  // Save the updated order with the "PAID" status

                    return "Payment Successful, Order has been updated!";
                }
                return "Payment session not found!";
            } else {
                return "Payment Failed!";
            }
        } catch (StripeException e) {
            // Handle Stripe exceptions
            e.printStackTrace();
            return "Payment verification failed due to an error with Stripe!";
        }

    }

    @GetMapping("/payment/cancel")
    public ResponseEntity<String> handlePaymentFailure() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
    }
}
