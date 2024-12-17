package com.ezen.books.domain;

import lombok.*;
import retrofit2.http.GET;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CouponVO {

    private long cno; // 쿠폰 번호
    private long gno; // 등급 번호
    private int minPrice; // 최소 구매 금액
    private int disAmount; // 할인 금액
    private int expDay; // 만료 일자
    
    /* sql 구문
    CREATE TABLE coupon (
    cno bigint NOT NULL AUTO_INCREMENT,
    gno int NOT NULL,
    min_price int,
    dis_amount int,
    exp_day int,
    PRIMARY KEY (cno)
    );*/
    
}
