package com.ezen.books.service;

import com.ezen.books.domain.CouponVO;

import java.util.List;

public interface CouponService {

    List<CouponVO> getAvailableCouponsForMember(long mno);

    void applyCouponToOrder(long mno, long cno);
}
