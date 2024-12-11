package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class ReviewVO {

//    CREATE TABLE review (
//    rno bigint NOT NULL AUTO_INCREMENT,
//    prno bigint NOT NULL,
//    mno bigint NOT NULL,
//    content VARCHAR(50) NOT NULL,
//    rating VARCHAR(50) NOT NULL,
//    reg_at DATETIME DEFAULT now(),
//    like_cnt int DEFAULT 0,
//    is_del VARCHAR(5) DEFAULT 'N',
//    PRIMARY KEY (rno)
//    );
//
//    ALTER TABLE review ADD COLUMN file_addr VARCHAR(500);
//    ALTER TABLE review MODIFY COLUMN rating DOUBLE(3,1) NOT NULL;
//    ALTER TABLE review MODIFY COLUMN content TEXT NOT NULL;

    private long rno;        // 리뷰 번호
    private long prno;       // 상품 번호
    private long mno;        // 회원 번호
    private String content;  // 리뷰 내용
    private double rating;   // 평점
    private String regAt;    // 등록 시간
    private int likeCnt;     // 좋아요 수
    private String isDel;    // 삭제 여부
    private String fileAddr; // 파일 주소

    // 테이블 외 변수
    private String name;

}
