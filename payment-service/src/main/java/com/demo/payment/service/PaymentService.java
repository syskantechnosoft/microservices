package com.demo.payment.service;

import com.demo.payment.domain.PaymentTransaction;
import com.demo.payment.dto.OrderCreatedEvent;
import com.demo.payment.dto.PaymentCompletedEvent;
import com.demo.payment.dto.PaymentRequest;
import com.demo.payment.dto.PaymentResponse;
import com.demo.payment.exception.PaymentException;
import com.demo.payment.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AtomicBoolean failMode = new AtomicBoolean(false);

    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public PaymentResponse processSync(PaymentRequest request) {
        return process(request.orderId(), request.username());
    }

    @KafkaListener(topics = "order-created", groupId = "payment-service")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        process(event.orderId(), event.username());
    }

    public PaymentResponse process(Long orderId, String username) {
        if (failMode.get()) {
            throw new PaymentException("Payment service is in simulated failure mode");
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(orderId);
        transaction.setUsername(username);
        transaction.setStatus("COMPLETED");
        transaction.setProcessedAt(Instant.now());
        paymentRepository.save(transaction);

        kafkaTemplate.send("payment-completed", new PaymentCompletedEvent(
                orderId, username, "COMPLETED", transaction.getProcessedAt()));

        return new PaymentResponse(orderId, "COMPLETED");
    }

    public boolean enableFailure() {
        failMode.set(true);
        return true;
    }

    public boolean disableFailure() {
        failMode.set(false);
        return false;
    }
}
