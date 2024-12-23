package com.ezen.books.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberVO {
        /* sql 구문
    CREATE TABLE member (
    mno bigint NOT NULL AUTO_INCREMENT,
    gno bigint NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    email VARCHAR(255),
    phone_number VARCHAR(30),
    nick_name VARCHAR(100),
    reg_at DATETIME default now(),
    last_login DATETIME,
    is_del VARCHAR(5) DEFAULT 'N',
    delete_reason VARCHAR(255),
    delete_date DATETIME,
    grade_log DATETIME,
    auth ENUM('ROLE_USER','RMANAGER','ADMIN'),
    PRIMARY KEY (mno)
    );
    ALTER TABLE member
    ADD COLUMN `provider` varchar(50) ,
    ADD COLUMN `provider_id` varchar(255);*/

    private long mno;

    @NotBlank(message = "ID를 입력하세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String isDel;
    private Date regAt;
    private Date lastLogin;
    private MemberAuth auth;

    // OAuth 제공자 (예: google, naver, kakao 등)
    private String provider;
    // OAuth 제공자에서 제공하는 고유 ID (예: google의 경우 userId)
    private String providerId;

    private String accessToken;
    private String refreshToken;

    private Long gno; // 등급 번호
    private Date gradeLog; // 등급 변경 일자

}
