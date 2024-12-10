package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private long adno;
    private long mno;
    private String recName;
    private String recPhone;
    private String addrCode;
    private String addr;
    private String addrDetail;
    private String addrName;
    private String isDefault;
}
