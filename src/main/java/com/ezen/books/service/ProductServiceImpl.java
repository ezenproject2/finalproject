package com.ezen.books.service;

import com.ezen.books.domain.ProductVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductMapper productMapper;

    @Transactional
    @Override
    public void testDataInsert(ProductVO productVO) {
        int valid = productMapper.isValid(productVO.getIsbn());
        if(valid == 0){
            int isOk = productMapper.registerTestData(productVO);
        }
    }

    @Override
    public int isValid(String isbn) {
        return productMapper.isValid(isbn);
    }

    @Override
    public int register(ProductVO productVO) {
        int isOk = productMapper.register(productVO);
        return isOk;
    }

    @Override
    public List<ProductVO> getList(PagingVO pagingVO) {
        List<ProductVO> list = productMapper.getList(pagingVO);
        return list;
    }

    @Override
    public int getTotalCount(PagingVO pagingVO) {
        return productMapper.getTotalCount(pagingVO);
    }

    @Override
    public ProductVO getDetail(String isbn) {
        return productMapper.getDetail(isbn);
    }
}
