package com.ezen.books.service;

import com.ezen.books.domain.BookProductDTO;

public interface ProductService {

    void testDataInsert(BookProductDTO bookProductDTO);

    int isValid(String isbn);

    int register(BookProductDTO bookProductDTO);
}
