package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class ProductVO {

//    CREATE TABLE product (
//    prno bigint NOT NULL AUTO_INCREMENT,
//    isbn VARCHAR(20) NOT NULL,
//    stock int DEFAULT 0,
//    discount_rate int DEFAULT 10,
//    profile_link VARCHAR(500),
//    detail_link VARCHAR(500),
//    primary_ctg VARCHAR(50) NOT NULL,
//    secondary_ctg VARCHAR(50) NOT NULL,
//    review_avg int DEFAULT 0,
//    sale_qty int DEFAULT 0,
//    is_del VARCHAR(5) DEFAULT 'N',
//    PRIMARY KEY (prno)
//    );

    private long prno;
    private String isbn;
    private int stock;
    private int discountRate;
    private String profileLink;
    private String detailLink;
    private String primaryCtg;
    private String secondaryCtg;
    private int reviewAvg;
    private int saleQty;
    private String isDel;

}
