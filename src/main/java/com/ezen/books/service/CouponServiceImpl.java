package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.CouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService{

    private final MemberService memberService;
    private final CouponMapper couponMapper;

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
    public List<CouponLogVO> findMemberCoupons(long mno) {
        return couponMapper.selectAvailableCoupons(mno);
    }

    @Override
    public CouponVO getCouponByCno(Long cno) {
        return couponMapper.getCouponByCno(cno);
    }

    @Override
    public CouponLogVO getCouponLogByMnoAndCno(Long mno, Long cno) {
        return couponMapper.getCouponLogByMnoAndCno(mno, cno);
    }

    @Override
    public void updateCouponLog(CouponLogVO newCouponLog) {
        couponMapper.updateCouponLog(newCouponLog);
    }

    @Override
    public List<CouponLogVO> findMemberAllCoupons(Long mno) {
        return couponMapper.findMemberAllCoupons(mno);
    }
}
