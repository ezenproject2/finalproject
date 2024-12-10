package com.ezen.books.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private long mno;
    private String id;
    private String pwd;
    private String type;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String address;
    private String createdAt;
    private String updateAt;
    private String lastLogin;
    private String isDeleted;
    private String delReason;
    private String delAt;
    private List<AuthVO> authList;

    // provider : google이 들어감
    private String provider;
    // providerId : 구굴 로그인 한 유저의 고유 ID가 들어감
    private String providerID;
}