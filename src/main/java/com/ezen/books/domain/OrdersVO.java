package com.ezen.books.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrdersVO {
    private long orno;
    private long mno;
    private String status;
    private int totalPrice;
    private LocalDateTime orderAt;
    private String isPickup;
}
/*
CREATE TABLE orders (
    orno bigint NOT NULL AUTO_INCREMENT,
    mno bigint NOT NULL,
    status VARCHAR(100) NOT NULL,
    total_price int NOT NULL,
    order_at DATETIME,
    is_pickup VARCHAR(5) DEFAULT 'N',
    PRIMARY KEY (orno)
);
*/