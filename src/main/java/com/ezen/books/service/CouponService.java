package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.GradeVO;

import java.util.List;

public interface CouponService {
    // 회원 쿠폰 조회
    List<CouponVO> getMemberCoupons(long mno);

    List<CouponLogVO> findMemberCoupons(long mno);

    boolean applyCouponToMember(long mno, long cno);


    CouponVO getCouponByCno(Long cno);

    CouponLogVO getCouponLogByMnoAndCno(Long mno, Long cno);

    void saveCouponLog(CouponLogVO newCouponLog);

    // 회원의 모든 쿠폰 사용 내역 조회
    List<CouponLogVO> findMemberAllCoupons(Long mno);
}
