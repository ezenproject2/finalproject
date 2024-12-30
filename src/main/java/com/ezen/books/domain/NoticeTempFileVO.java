package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class NoticeTempFileVO {

    //    CREATE TABLE notice_file (
    //    fino BIGINT AUTO_INCREMENT PRIMARY KEY,
    //    fileAddr VARCHAR(500) NOT NULL,
    //    ntno BIGINT,
    //    regAt datetime DEFAULT now()
    //    );

    private long fino;          // 파일 ID
    private String fileAddr;   // 파일 주소
    private long ntno;          // 게시글 번호
    private String regAt;      // 등록일

}
