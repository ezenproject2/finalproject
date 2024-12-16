package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDTO {
    private CartVO cartVO;
    private ProductVO productVO;
}