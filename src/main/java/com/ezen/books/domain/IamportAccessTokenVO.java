package com.ezen.books.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IamportAccessTokenVO {

    private String token;

    private int expiredAt;

    private int now;
}
