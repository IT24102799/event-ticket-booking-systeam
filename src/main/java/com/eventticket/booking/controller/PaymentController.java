package com.eventticket.booking.controller;

import com.eventticket.booking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payment")
    public String showPaymentPage() {
        return "payment";
    }

    @RestController
    @RequestMapping("/api/payments")
    public static class PaymentApiController {

        @Autowired
        private PaymentService paymentService;

        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> getAllPayments() {
            List<Map<String, Object>> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable String id) {
            Map<String, Object> payment = paymentService.getPaymentById(id);
            if (payment != null) {
                return ResponseEntity.ok(payment);
            }
            return ResponseEntity.notFound().build();
        }

        @PostMapping
        public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> paymentDetails) {
            Map<String, Object> payment = paymentService.processPayment(paymentDetails);
            return ResponseEntity.ok(payment);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, Object>> deletePayment(@PathVariable String id) {
            boolean deleted = paymentService.deletePayment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            return ResponseEntity.ok(response);
        }
    }
}
