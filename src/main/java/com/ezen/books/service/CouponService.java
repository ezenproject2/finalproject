package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.GradeVO;

import java.util.List;

public interface CouponService {
    // 회원 쿠폰 조회
    List<CouponVO> getMemberCoupons(long mno);

    // 쿠폰 적용
    void applyCoupon(long mno, long cno, String orno);

    // 사용 가능한 쿠폰 조회
    List<CouponVO> getAvailableCoupons(long gno, int purchaseAmount);

    // 회원 등급 조회
    GradeVO getMemberGrade(long mno);


    List<CouponLogVO> findMemberCoupons(long mno);
}
