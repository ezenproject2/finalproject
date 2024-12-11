package com.ezen.books.service;

import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.repository.ProductMapper;
import com.ezen.books.repository.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewMapper reviewMapper;
    private final ProductMapper productMapper;

    @Override
    public int register(ReviewVO reviewVO) {
        int isOk = reviewMapper.register(reviewVO);
        if(isOk>0){
            productMapper.updateReviewAvg(reviewVO.getPrno());
        }
        return isOk;
    }

    @Override
    public int getTotalCount(PagingVO pagingVO) {
        return reviewMapper.getTotalCount(pagingVO);
    }

    @Override
    public List<ReviewVO> getList(PagingVO pagingVO) {
        List<ReviewVO> list = reviewMapper.getList(pagingVO);
        for(ReviewVO reviewVO : list){
            // member 연결하면 넣을 예정
//            reviewVO.setName(reviewMapper.getName(reviewVO.getMno()));
            reviewVO.setName("홍길동");
        }
        return list;
    }

    @Override
    public int isLike(long rno, long mno) {
        int isOk = reviewMapper.isLike(rno, mno);
        return isOk;
    }
}
