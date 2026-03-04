package com.demo.order.controller;

import com.demo.order.dto.OrderResponse;
import com.demo.order.service.OrderService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Test
    void shouldGetOrder() {
        OrderService orderService = mock(OrderService.class);
        OrderController controller = new OrderController(orderService);
        when(orderService.get(1L)).thenReturn(new OrderResponse(1L, "CREATED", "PENDING"));
        OrderResponse response = controller.get(1L);
        assertThat(response.status()).isEqualTo("CREATED");
    }
}
