package com.demo.gateway.filter;

import com.demo.gateway.security.JwtValidator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthRateLimitFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public AuthRateLimitFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/v1/auth") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtValidator.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String user = jwtValidator.subject(token);
        if (isRateLimited(user)) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange.mutate()
                .request(exchange.getRequest().mutate().header("X-User-Id", user).build())
                .build());
    }

    private boolean isRateLimited(String user) {
        long nowEpochMinute = Instant.now().getEpochSecond() / 60;
        WindowCounter counter = counters.computeIfAbsent(user, ignored -> new WindowCounter(nowEpochMinute));
        synchronized (counter) {
            if (counter.epochMinute != nowEpochMinute) {
                counter.epochMinute = nowEpochMinute;
                counter.count.set(0);
            }
            return counter.count.incrementAndGet() > 100;
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private static class WindowCounter {
        private long epochMinute;
        private final AtomicInteger count = new AtomicInteger(0);

        private WindowCounter(long epochMinute) {
            this.epochMinute = epochMinute;
        }
    }
}
