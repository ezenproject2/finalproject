package com.ezen.books.service;

import com.ezen.books.domain.PointsVO;

import java.util.List;

public interface PointService {

    // 포인트 적립
    void earnPoints(long mno, String orno, int earnedPoints);

    // 포인트 사용
    void usePoints(long mno, String orno, int usePoints);

    // 포인트 잔액 조회
    int getBalance(long mno);

    // 포인트 내역 조회
    List<PointsVO> getPointHistory(long mno);

    // 포인트 추가
    void addPoints(long mno, int pointsEarned, String orno);
}
