package com.demo.user.config;

import com.demo.user.domain.Product;
import com.demo.user.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class SeedDataConfig {

    private final ProductRepository productRepository;

    public SeedDataConfig(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void seed() {
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("Laptop");
            p1.setPrice(BigDecimal.valueOf(1299));
            p1.setStock(25);
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setName("Headphones");
            p2.setPrice(BigDecimal.valueOf(199));
            p2.setStock(100);
            productRepository.save(p2);
        }
    }
}
