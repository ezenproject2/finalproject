package com.ezen.books.service;

import com.ezen.books.controller.NotificationController;
import com.ezen.books.domain.NotificationVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.repository.NotificationMapper;
import com.ezen.books.repository.ProductMapper;
import com.ezen.books.repository.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewMapper reviewMapper;
    private final ProductMapper productMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationController notificationController;

    @Transactional
    @Override
    public int register(ReviewVO reviewVO) {
        int isOk = reviewMapper.register(reviewVO);
        if(isOk>0){
            productMapper.updateReviewAvg(reviewVO.getPrno());
            productMapper.updateReviewCnt(reviewVO.getPrno(), 1);
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
            reviewVO.setIsLike(reviewMapper.isLike(reviewVO.getRno(), pagingVO.getMno()));
        }
        return list;
    }

    @Transactional
    @Override
    public int doLike(long rno, long mno) {
        // 좋아요
        int isOk = reviewMapper.doLike(rno, mno);
        if(isOk>0){
            // 동작 후 리뷰에 좋아요 수 업데이트
            isOk *= reviewMapper.updateCnt(rno, 1);
        }
        return isOk;
    }

    @Transactional
    @Override
    public int cancel(long rno, long mno) {
        int isOk = reviewMapper.cancel(rno, mno);
        if(isOk>0){
            isOk *= reviewMapper.updateCnt(rno, -1);
        }
        return isOk;
    }

    @Override
    public void createAndSendNotification(long mno, String message) {
        // 알림 객체 생성
        NotificationVO notification = new NotificationVO();
        notification.setMno(mno);
        notification.setMessage(message);
        notification.setStatus("UNREAD");

        // 알림 저장
        notificationMapper.insertNotification(notification);

        // 생성된 알림을 컨트롤러에 전달하여 실시간 전송
        notificationController.sendNotificationToClient(mno, notification);

    }
}
