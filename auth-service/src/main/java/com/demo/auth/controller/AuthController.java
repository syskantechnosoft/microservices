package com.demo.auth.controller;

import com.demo.auth.dto.AuthResponse;
import com.demo.auth.dto.LoginRequest;
import com.demo.auth.dto.RegisterRequest;
import com.demo.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/v1/auth/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/v1/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/v1/auth/validate")
    public Map<String, String> validate(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return Map.of("username", authService.validate(token));
    }

    @PostMapping("/v2/auth/login")
    public Map<String, Object> loginV2(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return Map.of("token", response.token(), "username", response.username(), "version", "v2");
    }
}
