package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class NoticeVO {

    //    CREATE TABLE notice (
    //    ntno BIGINT AUTO_INCREMENT PRIMARY KEY,
    //    title VARCHAR(255) NOT NULL,
    //    content TEXT NOT NULL,
    //    mno BIGINT,
    //    reg_at datetime DEFAULT now(),
    //    isDel varchar(5) DEFAULT 'N',
    //    category VARCHAR(50)
    //    );

    private long ntno;           // 게시글 번호
    private String title;        // 제목
    private String content;      // 내용
    private long mno;            // 작성자 번호
    private String regAt;        // 등록일
    private String isDel;        // 삭제 여부
    private String category;     // 카테고리

}
