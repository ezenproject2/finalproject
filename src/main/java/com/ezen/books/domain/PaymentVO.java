package com.ezen.books.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVO {
    private long pano;
    private long orno;
    private String measure;
    private int price;
    private String status;
    private LocalDateTime regAt;
    private String cardName;
    private String impUid;
}

/*
CREATE TABLE payment (
    pano bigint NOT NULL AUTO_INCREMENT,
    orno bigint NOT NULL,
    measure VARCHAR(50) NOT NULL,
    price int NOT NULL,
    status VARCHAR(100) NOT NULL,
    reg_at DATETIME NOT NULL,
    card_name VARCHAR(50),
    imp_uid VARCHAR(200) NOT NULL,
    uuid VARCHAR(250) NOT NULL,
    PRIMARY KEY (pano)
);
*/
