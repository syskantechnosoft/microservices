package com.demo.order.service;

import com.demo.order.domain.OrderEntity;
import com.demo.order.dto.*;
import com.demo.order.exception.OrderNotFoundException;
import com.demo.order.repository.OrderRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.clients.payment-service:http://payment-service}")
    private String paymentServiceBase;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    public OrderResponse create(CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setUsername(request.username());
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setStatus("CREATED");
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(Instant.now());
        order = orderRepository.save(order);

        kafkaTemplate.send("order-created", new OrderCreatedEvent(
                order.getId(), order.getUsername(), order.getProductId(), order.getQuantity(), order.getCreatedAt()));

        PaymentResponse paymentResponse = processPayment(new PaymentRequest(
                order.getId(), order.getUsername(), order.getProductId(), order.getQuantity())).join();

        order.setPaymentStatus(paymentResponse.paymentStatus());
        order.setStatus("PROCESSING");
        orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getStatus(), order.getPaymentStatus());
    }

    public OrderResponse get(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return new OrderResponse(order.getId(), order.getStatus(), order.getPaymentStatus());
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentService")
    @Bulkhead(name = "paymentService", type = Bulkhead.Type.SEMAPHORE)
    @TimeLimiter(name = "paymentService")
    public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> webClientBuilder.build().post()
                .uri(paymentServiceBase + "/api/v1/payments/process-sync")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block());
    }

    public CompletableFuture<PaymentResponse> paymentFallback(PaymentRequest request, Throwable ex) {
        return CompletableFuture.completedFuture(new PaymentResponse(request.orderId(), "FALLBACK_PENDING"));
    }
}
