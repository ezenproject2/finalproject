package com.ezen.books.service;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.PagingVO;

import java.util.List;

public interface ProductService {

    void testDataInsert(BookProductDTO bookProductDTO);

    int isValid(String isbn);

    int register(BookProductDTO bookProductDTO);

    List<BookProductDTO> getList(PagingVO pagingVO);

    int getTotalCount(PagingVO pagingVO);

    BookProductDTO getDetail(long prno);
}
