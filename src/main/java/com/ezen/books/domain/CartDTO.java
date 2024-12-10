package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private long mno;
    private long prno;
    private int bookQty;
}
