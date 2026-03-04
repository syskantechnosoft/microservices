package com.demo.order.service;

import com.demo.order.dto.PaymentRequest;
import com.demo.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OrderServiceTest {

    @Test
    void fallbackShouldReturnPendingStatus() {
        OrderService service = new OrderService(mock(OrderRepository.class), mock(KafkaTemplate.class), WebClient.builder());
        var response = service.paymentFallback(new PaymentRequest(1L, "u", 1L, 1), new RuntimeException()).join();
        assertThat(response.paymentStatus()).isEqualTo("FALLBACK_PENDING");
    }
}
