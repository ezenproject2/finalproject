package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private long odno;
    private String orno;
    private long prno;
    private int bookQty;
    private int price;
    private String status;
}
/*
CREATE TABLE order_detail (
    odno bigint NOT NULL AUTO_INCREMENT,
    orno VARCHAR(250) NOT NULL,
    prno bigint NOT NULL,
    book_qty int NOT NULL,
    price int NOT NULL,
    status VARCHAR(100) NOT NULL,
    PRIMARY KEY (odno)
);
 */