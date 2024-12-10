package com.ezen.books.repository;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ProductVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    int registerTestBookVO(BookVO bookVO);

    void registerTestProductVO(ProductVO productVO);

    int isValid(String isbn);

    int registerBookVO(BookVO bookVO);

    int registerProductVO(ProductVO productVO);

    List<ProductVO> getList(PagingVO pagingVO);

    BookVO getBookVO(String isbn);

    int getTotalCount(PagingVO pagingVO);

    ProductVO getDetail(long prno);
}
