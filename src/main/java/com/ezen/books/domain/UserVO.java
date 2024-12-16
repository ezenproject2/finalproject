package com.ezen.books.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    // NOTE: 장바구니, 결제 테스트용으로 만든 UserVO임.
    private long mno;
    private long gno;
    private String login_id;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickName;
    private LocalDateTime regAt;
    private LocalDateTime lastLogin;
    private String isDel;
    private String deleteReason;
    private LocalDateTime deleteDate;
    private LocalDateTime gradeLog;
    private String auth;
    private String provider;
    private String providerId;
}