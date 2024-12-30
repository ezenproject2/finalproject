package com.ezen.books.service;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ProductVO;

import java.util.List;

public interface ProductService {

    void testDataInsert(ProductVO productVO);

    int isValid(String isbn);

    int register(ProductVO productVO);

    List<ProductVO> getList(PagingVO pagingVO);

    int getTotalCount(PagingVO pagingVO);

    ProductVO getDetail(String isbn);

    List<ProductVO> getSpecialList(String type);

    void setBestTag();

    void setNewTag();

    void setHotTag();

    BookInfo getReviewInfo(long prno);

    ProductVO getData(long prno);
}
