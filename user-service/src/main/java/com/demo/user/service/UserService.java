package com.demo.user.service;

import com.demo.user.domain.Product;
import com.demo.user.dto.OrderResponse;
import com.demo.user.dto.PlaceOrderRequest;
import com.demo.user.dto.ProductResponse;
import com.demo.user.dto.ProductResponseV2;
import com.demo.user.exception.UserServiceException;
import com.demo.user.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final ProductRepository productRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.clients.order-service:http://order-service}")
    private String orderServiceBase;

    public UserService(ProductRepository productRepository, WebClient.Builder webClientBuilder) {
        this.productRepository = productRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<ProductResponse> productsV1() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }

    public List<ProductResponseV2> productsV2() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponseV2(p.getId(), p.getName(), p.getPrice(), p.getStock(), "USD"))
                .toList();
    }

    public OrderResponse placeOrder(String username, PlaceOrderRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new UserServiceException("Product not found"));
        if (product.getStock() < request.quantity()) {
            throw new UserServiceException("Insufficient stock");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("productId", request.productId());
        payload.put("quantity", request.quantity());

        OrderResponse response = webClientBuilder.build().post()
                .uri(orderServiceBase + "/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();

        if (response == null) {
            throw new UserServiceException("Order service unavailable");
        }
        return response;
    }
}
