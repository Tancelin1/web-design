package com.example.exo_12_tancelin.controller;

import com.example.exo_12_tancelin.model.Order;
import com.example.exo_12_tancelin.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrderById(@PathVariable Long id) {
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order order) {
        return service.createOrder(order);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Order>> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return service.updateOrder(id, order)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Order>> patchOrder(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        if (updates.containsKey("status")) {
            String status = updates.get("status");
            return service.updateOrderStatus(id, status)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable Long id) {
        return service.deleteOrder(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<Order> searchByStatus(@RequestParam String status) {
        return service.getOrdersByStatus(status);
    }

    @GetMapping("/customer/{customerName}")
    public Flux<Order> getByCustomer(@PathVariable String customerName) {
        return service.getOrdersByCustomer(customerName);
    }
}