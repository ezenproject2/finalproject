package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartVO {
    private long mno;
    private long prno;
    private int bookQty;
}
/*
CREATE TABLE cart (
    mno bigint NOT NULL,
    prno bigint NOT NULL,
    book_qty int NOT NULL
);
 */