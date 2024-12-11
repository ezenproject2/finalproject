package com.ezen.books.domain;

import com.ezen.books.handler.PagingHandler;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class ReviewPagingDTO {

    private List<ReviewVO> reviewVOList;
    private PagingHandler pagingHandler;

}
