package com.ezen.books.repository;

import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface ReviewMapper {

    int register(ReviewVO reviewVO);

    int getTotalCount(PagingVO pagingVO);

    List<ReviewVO> getList(PagingVO pagingVO);

    int isLike(@Param("rno") long rno, @Param("mno") long mno);
}
