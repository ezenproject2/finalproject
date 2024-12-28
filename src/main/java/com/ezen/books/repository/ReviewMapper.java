package com.ezen.books.repository;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface ReviewMapper {

    int register(ReviewVO reviewVO);

    int getTotalCount(PagingVO pagingVO);

    List<ReviewVO> getList(PagingVO pagingVO);

    int isLike(@Param("rno") long rno, @Param("mno") long mno);

    int doLike(@Param("rno") long rno, @Param("mno") long mno);

    int cancel(@Param("rno") long rno, @Param("mno") long mno);

    int updateCnt(@Param("rno") long rno, @Param("num") int i);

    BookInfo getReviewCnt(long prno);

    int getTotalCountByPrno(long prno);

    int getLikeCntByRno(long rno);

    int delete(long rno);

    long getPrnoByRno(@Param("rno") long rno);
}
