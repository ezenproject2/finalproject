package com.ezen.books.repository;

import com.ezen.books.domain.CouponVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<CouponVO> getCouponForGrade(Long gno);

    CouponVO getCouponById(long cno);
}
