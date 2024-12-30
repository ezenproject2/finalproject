package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailProductDTO {
    private OrderDetailVO orderDetailVO;
    private ProductVO productVO;
}
