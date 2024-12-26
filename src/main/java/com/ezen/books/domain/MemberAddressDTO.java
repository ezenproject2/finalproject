package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressDTO {
    // member의 필드들
    private String loginId;
    private String password;
    private String passwordCheck;
    private String email;
    private String name;
    private String phoneNumber;

    // address의 필드들
    private String addrCode;
    private String addr;
    private String addrDetail;
    private String nickName;
}
