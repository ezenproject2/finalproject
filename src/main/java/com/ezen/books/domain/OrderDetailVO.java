package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private long odno;
    private long orno;
    private long prno;
    private int bookQty;
    private int price;
}
/*
CREATE TABLE order_detail (
    odno bigint NOT NULL AUTO_INCREMENT,
    orno bigint NOT NULL,
    prno bigint NOT NULL,
    book_qty int NOT NULL,
    price int NOT NULL,
    PRIMARY KEY (odno)
);
 */