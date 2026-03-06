CREATE TABLE "orders" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    total_amount DOUBLE,
    status VARCHAR(50),
    created_at TIMESTAMP
);