package com.example.exo_12_tancelin.service;

import com.example.exo_12_tancelin.model.Order;
import com.example.exo_12_tancelin.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> getOrderById(Long id) {
        return repository.findById(id);
    }

    public Mono<Order> createOrder(Order order) {
        order.setStatus("PENDING");
        return repository.save(order);
    }

    public Mono<Order> updateOrderStatus(Long id, String status) {
        return repository.findById(id)
                .flatMap(order -> {
                    order.setStatus(status);
                    return repository.save(order);
                });
    }
    public Mono<Order> updateOrder(Long id, Order newOrderData) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setCustomerName(newOrderData.getCustomerName());
                    existing.setTotalAmount(newOrderData.getTotalAmount());
                    existing.setStatus(newOrderData.getStatus());
                    return repository.save(existing);
                });
    }

    public Mono<Void> deleteOrder(Long id) {
        return repository.deleteById(id);
    }

    public Flux<Order> getOrdersByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Flux<Order> getOrdersByCustomer(String customerName) {
        return repository.findByCustomerName(customerName);
    }
}