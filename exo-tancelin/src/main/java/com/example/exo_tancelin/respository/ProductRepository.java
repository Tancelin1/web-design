package com.example.exo_tancelin.repository;

import com.example.exo_tancelin.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProductRepository {

    private final Map<String, Product> products = new HashMap<>();

    public ProductRepository() {
        products.put("PROD001", new Product("PROD001", "Laptop", new BigDecimal("1200.00"), 10, "Electronics"));
        products.put("PROD002", new Product("PROD002", "Smartphone", new BigDecimal("800.00"), 5, "Electronics"));
        products.put("PROD003", new Product("PROD003", "Book", new BigDecimal("25.00"), 20, "Books"));
        products.put("PROD004", new Product("PROD004", "Desk Lamp", new BigDecimal("45.00"), 0, "Home"));
        products.put("PROD005", new Product("PROD005", "Chair", new BigDecimal("150.00"), 7, "Home"));
    }

    public Mono<Product> findById(String id) {
        return Mono.delay(Duration.ofMillis(100))
                .flatMap(ignore -> {
                    if (randomError()) return Mono.error(new RuntimeException("Random DB error"));
                    Product p = products.get(id);
                    return p != null ? Mono.just(p) : Mono.empty();
                });
    }

    public Flux<Product> findByIds(List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(this::findById);
    }

    public Mono<Integer> getStock(String productId) {
        return Mono.delay(Duration.ofMillis(100))
                .map(ignore -> {
                    if (randomError()) throw new RuntimeException("Random DB error");
                    Product p = products.get(productId);
                    return p != null ? p.getStock() : 0;
                });
    }

    private boolean randomError() {
        return ThreadLocalRandom.current().nextInt(10) == 0;
    }
}