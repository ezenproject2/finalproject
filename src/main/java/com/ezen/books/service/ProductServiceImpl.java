package com.ezen.books.service;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ProductVO;
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

    @Override
    public List<BookProductDTO> getList(PagingVO pagingVO) {
        List<ProductVO> voList = productMapper.getList(pagingVO);
        List<BookProductDTO> dtoList = new ArrayList<>();
        for(ProductVO productVO : voList){
            BookVO bookVO = productMapper.getBookVO(productVO.getIsbn());
            dtoList.add(new BookProductDTO(bookVO, productVO));
        }
        return dtoList;
    }

    @Override
    public int getTotalCount(PagingVO pagingVO) {
        return productMapper.getTotalCount(pagingVO);
    }

    @Override
    public BookProductDTO getDetail(long prno) {
        ProductVO productVO = productMapper.getDetail(prno);
        BookVO bookVO = productMapper.getBookVO(productVO.getIsbn());
        return new BookProductDTO(bookVO,productVO);
    }
}
