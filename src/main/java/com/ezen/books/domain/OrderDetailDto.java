package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private long odno;
    private long orno;
    private long prno;
    private int bookQty;
    private int price;
}
