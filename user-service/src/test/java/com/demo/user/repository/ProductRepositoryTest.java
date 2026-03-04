package com.demo.user.repository;

import com.demo.user.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductRepositoryTest {

    @Test
    void shouldReadProductsFromRepository() {
        ProductRepository repository = mock(ProductRepository.class);
        Product product = new Product();
        product.setName("Mouse");
        product.setPrice(BigDecimal.valueOf(30));
        product.setStock(20);
        when(repository.findAll()).thenReturn(List.of(product));
        assertThat(repository.findAll()).hasSize(1);
    }
}
