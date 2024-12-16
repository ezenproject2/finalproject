package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IamportAccessToken {

    private String token;
    private int expiredAt;
    private int now;
}
