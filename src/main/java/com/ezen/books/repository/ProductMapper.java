package com.ezen.books.repository;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.domain.PagingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    int registerTestData(ProductVO productVO);

    int isValid(String isbn);

    int register(ProductVO productVO);

    List<ProductVO> getList(PagingVO pagingVO);

    int getTotalCount(PagingVO pagingVO);

    ProductVO getDetail(String isbn);

    void updateReviewAvg(long prno);

    void updateReviewCnt(@Param("prno") long prno, @Param("num") int i);

    ProductVO chatGetProductVO(String userMessage);

    List<ProductVO> getSpecialList(String type);

    int deletePrevTag(String type);

    void setBestTag();

    void setNewTag();

    void setHotTag();

}
