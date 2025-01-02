package com.ezen.books.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InquiryVO {

    private long ino;
    private long mno;
    private String title;  // 제목
    private String type;  // 유형
    private String content;  // 내용
    private String fileAddr;   // 파일주소
    private String status;  // 상태(접수, 답변 대기, 완료,취소)
    private String response;  // 답변 내용
    private Date regAt;  // 작성일
    private Date modAt;  // 답변일

    // sql 구문 외
    private String loginId;

}
