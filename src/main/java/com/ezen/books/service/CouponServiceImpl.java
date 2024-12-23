package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.CouponLogMapper;
import com.ezen.books.repository.CouponMapper;
import com.ezen.books.repository.GradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService{

    private final MemberService memberService;
    private final CouponMapper couponMapper;
    private final CouponLogMapper couponLogMapper;
    private final GradeMapper gradeMapper;

    @Override
    public List<CouponVO> getMemberCoupons(long mno) {
        MemberVO memberVO = memberService.getMemberById(mno);
        // memberVO가 null인 경우 예외 처리 또는 다른 처리
        if (memberVO == null) {
            // 예외를 던지거나, 빈 리스트를 반환
            throw new RuntimeException("회원 정보를 찾을 수 없습니다. mno: " + mno);
        }
        return couponMapper.getCouponsForGrade(memberVO.getGno());
    }

    @Override
    public void applyCoupon(long mno, long cno, String orno) {

        couponMapper.applyCoupon(mno, cno, orno);  // 쿠폰을 주문에 적용

        /*----------------------------*/

        CouponVO couponVO = couponMapper.getCouponById(cno);
        CouponLogVO couponLogVO = new CouponLogVO();
        couponLogVO.setMno(mno);
        couponLogVO.setCno(cno);
        couponLogVO.setStatus("사용 완료");
        couponLogVO.setUsedAt(new Date());
        couponLogVO.setExpAt(couponVO.getExpDay());
        couponLogMapper.insertCouponLog(couponLogVO);

    }

    @Override
    public List<CouponVO> getAvailableCoupons(long gno, int purchaseAmount) {
        // 해당 회원 등급과 구매 금액에 맞는 쿠폰을 조회
        return couponMapper.getCouponsByGrade(gno, purchaseAmount);
    }

    @Override
    public GradeVO getMemberGrade(long mno) {
        return gradeMapper.getGradeByMember(mno);
    }
}
