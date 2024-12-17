package com.ezen.books.service;

import com.ezen.books.domain.PointsVO;

import java.util.List;

public interface PointService {

    void earnPoints(long mno, long orno, int earnedPoints);


    void usePoints(long mno, long orno, int usePoints);


    int getBalance(long mno);


    List<PointsVO> getPointHistory(long mno);


    void addPoints(long mno, int pointsEarned, long orno);
}
