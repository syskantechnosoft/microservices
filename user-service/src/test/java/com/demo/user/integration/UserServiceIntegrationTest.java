package com.demo.user.integration;

import com.demo.user.domain.Product;
import com.demo.user.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@Disabled("Enable when Docker daemon is available for Testcontainers")
class UserServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3")
            .withDatabaseName("userdb")
            .withUsername("app")
            .withPassword("app");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private ProductRepository repository;

    @Test
    void contextLoadsAndPersistsWithContainer() {
        Product p = new Product();
        p.setName("Container Product");
        p.setPrice(BigDecimal.ONE);
        p.setStock(1);
        repository.save(p);

        assertThat(repository.findAll()).isNotEmpty();
    }
}
