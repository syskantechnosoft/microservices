package com.demo.payment.controller;

import com.demo.payment.dto.PaymentRequest;
import com.demo.payment.dto.PaymentResponse;
import com.demo.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/v1/payments/process-sync")
    public PaymentResponse processSync(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processSync(request);
    }

    @PostMapping("/api/payment/fail")
    public Map<String, Object> fail() {
        return Map.of("failMode", paymentService.enableFailure());
    }

    @PostMapping("/api/payment/recover")
    public Map<String, Object> recover() {
        return Map.of("failMode", paymentService.disableFailure());
    }
}
