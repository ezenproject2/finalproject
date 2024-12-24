package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryVO {
    private String orno;
    private String recName;
    private String recPhone;
    private String addrCode;
    private String addr;
    private String addrDetail;
    private String addrMemo;
}
/*
DROP TABLE IF EXISTS delivery;
CREATE TABLE delivery (
    deno bigint NOT NULL AUTO_INCREMENT,
    orno VARCHAR(250) NOT NULL,
    rec_name VARCHAR(50) NOT NULL,
    rec_phone VARCHAR(50) NOT NULL,
    addr_code VARCHAR(50) NOT NULL,
    addr VARCHAR(50) NOT NULL,
    addr_detail VARCHAR(255) NOT NULL,
    addr_memo VARCHAR(255),
    PRIMARY KEY (deno)
);
*/