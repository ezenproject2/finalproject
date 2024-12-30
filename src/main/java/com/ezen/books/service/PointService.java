package com.ezen.books.service;

import com.ezen.books.domain.PointsVO;

import java.util.List;

public interface PointService {

    int getBalance(long mno);

    void savePoint(PointsVO pointsVO);

    List<PointsVO> getPointsHistory(long mno);
}
