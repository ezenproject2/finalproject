package com.ezen.books.service;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.repository.ProductMapper;
import com.ezen.books.repository.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductMapper productMapper;
    private final ReviewMapper reviewMapper;

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

    @Override
    public List<ProductVO> getSpecialList(String type) {
        return productMapper.getSpecialList(type);
    }

    @Override
    public void setBestTag() {
        int isOk = productMapper.deletePrevTag("best");
        if(isOk>0){
            log.info(">>>> 태그 자동 설정 best");
            productMapper.setBestTag();
        }
    }

    @Override
    public void setNewTag() {
        int isOk = productMapper.deletePrevTag("new");
        if(isOk>0){
            log.info(">>>> 태그 자동 설정 new");
            productMapper.setNewTag();
        }
    }

    @Override
    public void setHotTag() {
        int isOk = productMapper.deletePrevTag("hot");
        if(isOk>0){
            log.info(">>>> 태그 자동 설정 hot");
            productMapper.setHotTag();
        }
    }

    @Override
    public BookInfo getReviewInfo(long prno) {
        BookInfo bookInfo = reviewMapper.getReviewCnt(prno);
        int totalCount = reviewMapper.getTotalCountByPrno(prno);
        if (totalCount > 0) {
            bookInfo.setPer1((int) Math.floor((double) bookInfo.getCnt1() / totalCount * 100));
            bookInfo.setPer2((int) Math.floor((double) bookInfo.getCnt2() / totalCount * 100));
            bookInfo.setPer3((int) Math.floor((double) bookInfo.getCnt3() / totalCount * 100));
            bookInfo.setPer4((int) Math.floor((double) bookInfo.getCnt4() / totalCount * 100));
            bookInfo.setPer5((int) Math.floor((double) bookInfo.getCnt5() / totalCount * 100));
        }
        return bookInfo;
    }
}
