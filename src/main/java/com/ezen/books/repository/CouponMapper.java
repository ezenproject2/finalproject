package com.ezen.books.repository;

import com.ezen.books.domain.CouponVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {

    CouponVO getCouponById(long cno);

    List<CouponVO> getCouponsByGrade(long gno, int purchaseAmount);

    /*------------------------------------------------------------*/
    List<CouponVO> getCouponsByGradeAndPrice(long gno, int totalPrice);

    void applyCoupon(long mno, long cno, String orno);

    List<CouponVO> getCouponsForGrade(Long gno);
}
