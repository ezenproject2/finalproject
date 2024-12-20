package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class Notification {

//    CREATE TABLE notification (
//    nfno BIGINT AUTO_INCREMENT PRIMARY KEY,
//    mno BIGINT NOT NULL,
//    message TEXT NOT NULL,
//    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
//    reg_at datetime DEFAULT now(),
//    mod_at datetime DEFAULT now()
//    );

    // 테이블 변수
    private Long nfno;        // 알림 고유 ID
    private Long mno;         // 알림을 받을 사용자 ID
    private String message;   // 알림 메시지
    private String status;    // 알림 상태 (READ / UNREAD)
    private String regAt;     // 생성 시간
    private String modAt;     // 수정 시간

}
