package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class BookProductDTO {

    private BookVO bookVO;
    private ProductVO productVO;

}
