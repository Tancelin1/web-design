package com.example.exo_12_tancelin.repository;

import com.example.exo_12_tancelin.model.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    Flux<Order> findByStatus(String status);
    Flux<Order> findByCustomerName(String customerName);
}