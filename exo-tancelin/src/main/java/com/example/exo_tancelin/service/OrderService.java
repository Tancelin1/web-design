package com.example.exo_tancelin.service;

import com.example.exo_tancelin.model.*;
import com.example.exo_tancelin.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

public class OrderService {

    private static final Logger logger = Logger.getLogger(OrderService.class.getName());
    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Order> processOrder(OrderRequest request) {
        if (request.getCustomerId() == null || request.getCustomerId().isEmpty()
                || request.getProductIds() == null || request.getProductIds().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Invalid order request"));
        }

        return Flux.fromIterable(request.getProductIds())
                .filter(id -> id != null && !id.isEmpty())
                .take(100)
                .flatMap(id ->
                        productRepository.findById(id)
                                .onErrorResume(e -> {
                                    logger.warning("Erreur pour le produit " + id + ": " + e.getMessage());
                                    return Mono.empty();
                                })
                )
                .filter(product -> product.getStock() > 0)
                .map(product -> {
                    int discount = "Electronics".equalsIgnoreCase(product.getCategory()) ? 10 : 5;
                    BigDecimal finalPrice = product.getPrice()
                            .multiply(BigDecimal.valueOf(100 - discount))
                            .divide(BigDecimal.valueOf(100));
                    return new ProductWithPrice(product, product.getPrice(), discount, finalPrice);
                })
                .collectList()
                .map(productsWithPrice -> {
                    Order order = new Order(request.getProductIds());
                    order.setProducts(productsWithPrice);
                    BigDecimal total = productsWithPrice.stream()
                            .map(ProductWithPrice::getFinalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    order.setTotalPrice(total);
                    order.setDiscountApplied(true);
                    order.setStatus(OrderStatus.COMPLETED);
                    return order;
                })
                .timeout(java.time.Duration.ofSeconds(5))
                .doOnError(e -> logger.severe("Erreur dans le pipeline: " + e.getMessage()))
                .onErrorResume(e -> {
                    Order failedOrder = new Order(request.getProductIds());
                    failedOrder.setStatus(OrderStatus.FAILED);
                    return Mono.just(failedOrder);
                })
                .doOnNext(order -> logger.info("Order process terminé: " + order.getOrderId()))
                .doFinally(signal -> logger.info("Fin du traitement de la commande"));
    }
}