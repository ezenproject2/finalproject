package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class ProductVO {

//    CREATE TABLE book (
//    isbn VARCHAR(20) NOT NULL,
//    title VARCHAR(255),
//    link VARCHAR(500),
//    image VARCHAR(500),
//    author VARCHAR(255),
//    discount int NOT NULL,
//    publisher VARCHAR(100),
//    pubdate VARCHAR(20),
//    description TEXT,
//    is_del VARCHAR(5) DEFAULT 'N',
//    PRIMARY KEY (isbn)
//    );

//    변경 후
//    CREATE TABLE book (
//    isbn VARCHAR(20) NOT NULL,
//    stock INT DEFAULT 0,
//    discount_rate INT DEFAULT 10,
//    primary_ctg VARCHAR(50),
//    secondary_ctg VARCHAR(50),
//    review_avg INT DEFAULT 0,
//    sale_qty INT DEFAULT 0,
//    is_del VARCHAR(5) DEFAULT 'N',
//    title VARCHAR(255) NOT NULL,
//    link VARCHAR(500) NOT NULL,
//    image VARCHAR(500),
//    author VARCHAR(255),
//    discount INT NOT NULL,
//    publisher VARCHAR(100),
//    pubdate VARCHAR(20),
//    description TEXT,
//    PRIMARY KEY (isbn)
//    );

    private String isbn;             // ISBN
    private String title;            // 책 제목
    private String link;             // 링크
    private String image;            // 이미지 URL
    private String author;           // 저자
    private int discount;            // 할인율
    private String publisher;        // 출판사
    private String pubdate;          // 출판일
    private String description;      // 책 설명
    private String isDel;            // 삭제 여부 (기본값: 'N')
    private int stock;               // 재고 (기본값: 0)
    private int discountRate;        // 할인율 (기본값: 10)
    private String primaryCtg;       // 주요 카테고리
    private String secondaryCtg;     // 세부 카테고리
    private int reviewAvg;           // 리뷰 평균 (기본값: 0)
    private int saleQty;             // 판매 수량 (기본값: 0)

    private int isValid;
}
