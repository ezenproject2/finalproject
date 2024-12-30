package com.ezen.books.service;

import com.ezen.books.controller.NotificationController;
import com.ezen.books.domain.NotificationVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.repository.MemberMapper;
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
    private final MemberMapper memberMapper;

    @Transactional
    @Override
    public int register(ReviewVO reviewVO) {
        // 리뷰 등록
        int isOk = reviewMapper.register(reviewVO);
        if(isOk>0){
            // 등록 후 상품 별점, 리뷰 수 업데이트
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
        // 리뷰 출력
        List<ReviewVO> list = reviewMapper.getList(pagingVO);
        for(ReviewVO reviewVO : list){
            reviewVO.setName(memberMapper.getNickName(reviewVO.getMno()));
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
            isOk = reviewMapper.getLikeCntByRno(rno);

            // 리뷰 작성자에게 알림 데이터 저장시키기
            long writerMno = reviewMapper.getMnoByRno(rno);
            NotificationVO notificationVO = NotificationVO.builder()
                    .mno(writerMno)
                    .message("회원님의 리뷰에 누군가 좋아요를 눌렀습니다!")
                    .type("리뷰")
                    .rno(rno)
                    .build();

            // 알림 저장
            notificationMapper.insertNotificationRVer(notificationVO);

            // 생성된 알림을 컨트롤러에 전달하여 실시간 전송
//            notificationController.sendNotificationToClient(mno, notificationVO);
        }
        return isOk;
    }

    @Transactional
    @Override
    public int cancel(long rno, long mno) {
        int isOk = reviewMapper.cancel(rno, mno);
        if(isOk>0){
            isOk *= reviewMapper.updateCnt(rno, -1);
            isOk = reviewMapper.getLikeCntByRno(rno);
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

    @Transactional
    @Override
    public int delete(long rno) {
        // 리뷰 삭제 전 rno 를 통해 prno 가져와 저장
        long prno = reviewMapper.getPrnoByRno(rno);

        // 리뷰 삭제
        int isOk = reviewMapper.delete(rno);

        if(isOk>0){
            // 리뷰 삭제 후 상품 별점, 리뷰 수 업데이트
            productMapper.updateReviewAvg(prno);
            productMapper.updateReviewCnt(prno, -1);
        }
        return isOk;
    }

    @Override
    public int checkReviewd(long mno, long prno) {
        return reviewMapper.checkReviewd(mno, prno);
    }
}
