package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class OfflineBookVO {

//    CREATE TABLE offline_book (
//            osno bigint NOT NULL,
//            prno bigint NOT NULL,
//            stock int DEFAULT 0
//    );

    private long osno;   // 매장 번호
    private long prno;   // 상품 번호
    private int stock;   // 재고 수량

}
