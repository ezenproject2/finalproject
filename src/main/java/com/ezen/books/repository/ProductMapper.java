package com.ezen.books.repository;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.domain.ProductVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {

    int registerTestBookVO(BookVO bookVO);

    void registerTestProductVO(ProductVO productVO);

    int isValid(String isbn);

    int registerBookVO(BookVO bookVO);

    int registerProductVO(ProductVO productVO);
}
