package com.ezen.books.repository;

import com.ezen.books.domain.PointsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointMapper {

    PointsVO getLastPointByMember(long mno);

    void savePoints(PointsVO pointsVO);

    List<PointsVO> getPointsHistory(long mno);

    Integer getExpiringPoints();

    Integer getPointAmount(String orno);
}
