package com.demo.user.controller;

import com.demo.user.dto.ProductResponse;
import com.demo.user.service.UserService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class UserControllerTest {

    @Test
    void shouldReturnProducts() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);
        when(userService.productsV1()).thenReturn(List.of(new ProductResponse(1L, "Book", BigDecimal.TEN)));
        assertThat(controller.productsV1()).hasSize(1);
        assertThat(controller.productsV1().get(0).name()).isEqualTo("Book");
    }
}
