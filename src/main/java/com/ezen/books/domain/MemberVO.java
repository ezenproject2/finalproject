package com.ezen.books.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberVO {

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
    private String regAt;
    private String lastLogin;
    private MemberAuth auth;

    // OAuth 제공자 (예: google, naver, kakao 등)
    private String provider;
    // OAuth 제공자에서 제공하는 고유 ID (예: google의 경우 userId)
    private String providerId;

    // 액세스 토큰
    private String accessToken;
    // 리프레시 토큰
    private String refreshToken;


}
