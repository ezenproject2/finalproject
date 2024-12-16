package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class OfflineStoreVO {

//    CREATE TABLE offline_store (
//    osno bigint NOT NULL AUTO_INCREMENT,
//    name VARCHAR(50),
//    address VARCHAR(255),
//    phone VARCHAR(20),
//    PRIMARY KEY (osno)
//    );
//
//    ALTER TABLE offline_store ADD COLUMN hours VARCHAR(100), ADD COLUMN holiday VARCHAR(100);

    private Long osno;      // 오프라인 매장 번호
    private String name;    // 매장 이름
    private String address; // 매장 주소
    private String phone;   // 전화번호
    private String hours;   // 운영 시간
    private String holiday; // 휴일

}
