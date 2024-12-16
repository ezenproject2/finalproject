package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductVO {
    // NOTE: 장바구니, 결제 테스트용으로 만든 ProductDTO임.
    private long prno;
    private String isbn;
    private int stock;
    private int discountRate; // NULLABLE
    private String primaryCtg;
    private String secondaryCtg;
    private double reviewAvg;
    private long reviewCnt;
    private int saleQty;
    private String isDel;
    private String title;
    private String link;
    private String image;
    private String author;
    private int discount; // NOT NULL. 얘가 price인듯...
    private String pubDate;
    private String description;
}

/*
CREATE TABLE product (
    prno BIGINT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL,
    stock INT DEFAULT 0,
    discount_rate INT DEFAULT 10,
    primary_ctg VARCHAR(50),
    secondary_ctg VARCHAR(50),
    review_avg DOUBLE(3,1) DEFAULT 0,
    review_cnt bigint default 0,
    sale_qty INT DEFAULT 0,
    is_del VARCHAR(5) DEFAULT 'N',
    title VARCHAR(255) NOT NULL,
    link VARCHAR(500) NOT NULL,
    image VARCHAR(500),
    author VARCHAR(255),
    discount INT NOT NULL,
    publisher VARCHAR(100),
    pubdate VARCHAR(20),
    description TEXT,
    PRIMARY KEY (prno)
);
*/