package com.ezen.books.service;

import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;

import java.util.List;

public interface ReviewService {

    int register(ReviewVO reviewVO);

    int getTotalCount(PagingVO pagingVO);

    List<ReviewVO> getList(PagingVO pagingVO);

    int doLike(long rno, long mno);

    int cancel(long rno, long mno);
}
