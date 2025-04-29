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

    /**
     * Handles successful payment redirection from Stripe.
     * <p>
     * Verifies the token, retrieves the corresponding order and payment,
     * checks payment status via Stripe API, updates records if necessary,
     * and redirects the user to the appropriate success or failure page.
     *
     * @param token The unique success token associated with the order.
     * @return HTTP redirection to the success or failure page.
     */
    @GetMapping("/payment/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam("token") String token) {

        if (token == null) {
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }

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

    /**
     * Handles canceled or failed payments.
     * <p>
     * Returns a simple error message indicating that the payment attempt was unsuccessful.
     *
     * @return HTTP 400 (Bad Request) with an error message.
     */
    @GetMapping("/payment/cancel")
    public ResponseEntity<String> handlePaymentFailure() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
    }
}
