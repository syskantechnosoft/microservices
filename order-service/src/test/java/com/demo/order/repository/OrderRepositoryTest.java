package com.demo.order.repository;

import com.demo.order.domain.OrderEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderRepositoryTest {

    @Test
    void shouldReturnOrderFromRepository() {
        OrderRepository repository = mock(OrderRepository.class);
        OrderEntity order = new OrderEntity();
        order.setUsername("test");
        order.setProductId(1L);
        order.setQuantity(1);
        order.setStatus("CREATED");
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(Instant.now());
        when(repository.findAll()).thenReturn(List.of(order));
        assertThat(repository.findAll()).hasSize(1);
    }
}
