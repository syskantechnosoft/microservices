package com.demo.user.service;

import com.demo.user.domain.Product;
import com.demo.user.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void productsV1ShouldMapEntities() {
        ProductRepository repository = mock(ProductRepository.class);
        WebClient.Builder builder = WebClient.builder();
        UserService service = new UserService(repository, builder);

        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(10));
        product.setStock(1);

        when(repository.findAll()).thenReturn(List.of(product));

        assertThat(service.productsV1()).hasSize(1);
        assertThat(service.productsV1().get(0).name()).isEqualTo("Phone");
    }
}
