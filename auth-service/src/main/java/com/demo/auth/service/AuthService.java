package com.demo.auth.service;

import com.demo.auth.domain.AuthUser;
import com.demo.auth.dto.AuthResponse;
import com.demo.auth.dto.LoginRequest;
import com.demo.auth.dto.RegisterRequest;
import com.demo.auth.exception.AuthException;
import com.demo.auth.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new AuthException("Username already exists");
        }
        AuthUser user = new AuthUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        repository.save(user);
        return new AuthResponse(jwtService.generate(user.getUsername()), user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        AuthUser user = repository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }
        return new AuthResponse(jwtService.generate(user.getUsername()), user.getUsername());
    }

    public String validate(String token) {
        return jwtService.validate(token);
    }
}
