package com.ezen.books.domain;

import lombok.*;
import retrofit2.http.GET;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PointsVO {
    /* sql 구문
    CREATE TABLE points (
    pno bigint NOT NULL AUTO_INCREMENT,
    mno bigint NOT NULL,
    orno bigint NOT NULL,
    earned int,
    used int,
    balance int NOT NULL,
    reg_at DATE,
    PRIMARY KEY (pno)
    );*/

    private long pno; // 포인트 번호
    private long mno; // 회원 번호
    private long orno; // 주문 번호
    private int earned; // 적립된 포인트
    private int used; // 사용된 포인트
    private int balance; // 잔액
    private Date regAt; // 포인트 적립 일자
}
