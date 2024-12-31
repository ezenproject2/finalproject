package com.ezen.books.service;

import com.ezen.books.domain.PointsVO;
import com.ezen.books.repository.PointMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PointServiceImpl implements PointService{

    private final PointMapper pointMapper;

    @Override
    public int getBalance(long mno) {
        PointsVO lastPoint = pointMapper.getLastPointByMember(mno);
        return (lastPoint != null) ? lastPoint.getBalance() : 0;
    }

    @Override
    public void savePoint(PointsVO pointsVO) {
        pointMapper.savePoints(pointsVO);
    }

    @Override
    public List<PointsVO> getPointsHistory(long mno) {
        return pointMapper.getPointsHistory(mno);
    }

    @Override
    public int getExpiringPoints() {
        Integer points = pointMapper.getExpiringPoints();
        return points != null ? points : 0;
    }

    @Override
    public int getPointAmount(String orno) {
        Integer amount = pointMapper.getPointAmount(orno);
        return (amount != null) ? amount : 0;
    }

}
