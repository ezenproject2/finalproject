package com.ezen.books.repository;

import com.ezen.books.domain.PointsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointMapper {
    void insertPoint(PointsVO pointsVO);

    void updateBalace(PointsVO pointsVO);

    PointsVO getLastPointByMember(long mno);

    List<PointsVO> getPointByMember(long mno);

    void savePoints(PointsVO pointsVO);
}
