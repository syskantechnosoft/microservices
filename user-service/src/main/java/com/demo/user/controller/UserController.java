package com.demo.user.controller;

import com.demo.user.dto.OrderResponse;
import com.demo.user.dto.PlaceOrderRequest;
import com.demo.user.dto.ProductResponse;
import com.demo.user.dto.ProductResponseV2;
import com.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/v1/products")
    public List<ProductResponse> productsV1() {
        return userService.productsV1();
    }

    @GetMapping("/v2/products")
    public List<ProductResponseV2> productsV2() {
        return userService.productsV2();
    }

    @PostMapping("/v1/users/orders")
    public OrderResponse placeOrder(@RequestHeader("X-User-Id") String username,
                                    @Valid @RequestBody PlaceOrderRequest request) {
        return userService.placeOrder(username, request);
    }
}
