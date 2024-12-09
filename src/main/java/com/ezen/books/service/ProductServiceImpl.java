package com.ezen.books.service;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductMapper productMapper;

    @Transactional
    @Override
    public void testDataInsert(BookProductDTO bookProductDTO) {
        int valid = productMapper.isValid(bookProductDTO.getBookVO().getIsbn());
        if(valid == 0){
            int isOk = productMapper.registerTestBookVO(bookProductDTO.getBookVO());
            if(isOk>0){
                productMapper.registerTestProductVO(bookProductDTO.getProductVO());
            }
        }
    }

    @Override
    public int isValid(String isbn) {
        return productMapper.isValid(isbn);
    }

    @Transactional
    @Override
    public int register(BookProductDTO bookProductDTO) {
        int isOk = productMapper.registerBookVO(bookProductDTO.getBookVO());
        if(isOk>0){
            isOk *= productMapper.registerProductVO(bookProductDTO.getProductVO());
        }
        return isOk;
    }
}
