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
    private final GradeService gradeService;    // 등급 서비스 추가 (포인트 비율 조회용)

    @Override
    public void earnPoints(long mno, String orno, int earnedPoints) {
        // 등급에 따른 포이트 적립 비율 계산
        int pointRate = gradeService.getPointRate(mno); //회원 등급에 따른 포인트 비율
        int calculatedPoints = (earnedPoints * pointRate) / 100;    //적립할 포인트 계산

        PointsVO pointsVO = new PointsVO();
        pointsVO.setMno(mno);
        pointsVO.setOrno(orno);
        pointsVO.setEarned(earnedPoints);
        pointsVO.setUsed(0);
        pointsVO.setBalance(getBalance(mno) + earnedPoints);    // 잔액 갱신
        pointsVO.setRegAt(new Date());
        pointMapper.insertPoint(pointsVO);
    }

    @Override
    public void usePoints(long mno, String orno, int usePoints) {
        int currentBalance = getBalance(mno);
        if(currentBalance < usePoints){
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        PointsVO pointsVO = new PointsVO();
        pointsVO.setMno(mno);
        pointsVO.setOrno(orno);
        pointsVO.setEarned(0);
        pointsVO.setUsed(usePoints);
        pointsVO.setBalance(currentBalance - usePoints);    // 잔액 차감
        pointsVO.setRegAt(new Date());
        pointMapper.insertPoint(pointsVO);
        pointMapper.updateBalace(pointsVO);     // 잔액 업데이트
    }

    @Override
    public int getBalance(long mno) {
        PointsVO lastPoint = pointMapper.getLastPointByMember(mno);
        return (lastPoint != null) ? lastPoint.getBalance() : 0;
    }

    @Override
    public List<PointsVO> getPointHistory(long mno) {
        return pointMapper.getPointByMember(mno);
    }

    @Override
    public void addPoints(long mno, int pointsEarned, String orno) {

    }
}
