package com.ezen.books.repository;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    List<CouponVO> getCouponsForGrade(Long gno);

    List<CouponLogVO> selectAvailableCoupons(long mno);

    int applyCoupon(@Param("mno") long mno, @Param("cno") long cno);

    CouponVO getCouponByCno(Long cno);

    CouponLogVO getCouponLogByMnoAndCno(Long mno, Long cno);

    void saveCouponLog(CouponLogVO newCouponLog);

    List<CouponLogVO> findMemberAllCoupons(Long mno);
}
