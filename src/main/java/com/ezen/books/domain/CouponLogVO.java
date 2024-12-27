package com.ezen.books.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CouponLogVO {
    /* sql 구문
    CREATE TABLE coupon_log (
    clno bigint NOT NULL AUTO_INCREMENT,
    mno bigint NOT NULL,
    orno bigint NOT NULL,
    cno bigint NOT NULL,
    status ENUM('사용 가능', '사용 완료', '만료') NOT NULL,
    used_at DATE,
    exp_at DATE,
    PRIMARY KEY (clno)
    );*/

    private long clno; // 쿠폰 기록 번호
    private long mno; // 회원 번호
    private String orno; // 주문 번호
    private long cno; // 쿠폰 번호
    private String status; // 상태 (사용 가능, 사용 완료, 만료)
    private Date usedAt; // 사용 일자
    private Date expAt; // 만료 일자

    // Coupon 테이블의 추가 필드
    private String couponName;
    private long disAmount;
    private long minPrice;




}
