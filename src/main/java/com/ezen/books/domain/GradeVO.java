package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GradeVO {

    private long gno;  // 등급 번호
    private String level; // 등급 (새싹, 실버, 골드, 플래티넘)
    private int pointRate; // 포인트 비율
    private int period; // 등급 유지 기간

    /* sql 구문
    CREATE TABLE grade (
    gno bigint NOT NULL AUTO_INCREMENT,
    level ENUM('새싹', '실버', '골드', '플래티넘') NOT NULL,
    period int,
    point_rate int,
    PRIMARY KEY (gno)
    );*/

}
