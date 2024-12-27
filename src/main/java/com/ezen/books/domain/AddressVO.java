package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressVO {
    private long adno;
    private long mno;
    private String recName;
    private String recPhone;
    private String addrCode;
    private String addr;
    private String addrDetail;
    private String addrName;
    private String isDefault;

    // DB 컬럼과 대응하지 않는 필드.
    private String defaultOrNot;
}
/*
CREATE TABLE address (
    adno bigint NOT NULL AUTO_INCREMENT,
    mno bigint NOT NULL,
    rec_name VARCHAR(50) NOT NULL,
    rec_phone VARCHAR(50) NOT NULL,
    addr_code VARCHAR(50) NOT NULL,
    addr VARCHAR(50) NOT NULL,
    addr_detail VARCHAR(255) NOT NULL,
    addr_name VARCHAR(50),
    is_default VARCHAR(5) DEFAULT 'N',
    PRIMARY KEY (adno)
);
 */