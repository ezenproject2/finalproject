package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class BookVO {

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

    private String isbn;
    private String title;
    private String link;
    private String image;
    private String author;
    private int discount;
    private String publisher;
    private String pubdate;
    private String description;
    private String isDel;

    private int isValid;
}
