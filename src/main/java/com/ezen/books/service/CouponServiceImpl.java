package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.CouponLogMapper;
import com.ezen.books.repository.CouponMapper;
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

    @Override
    public List<CouponVO> getAvailableCouponsForMember(long mno) {
        MemberVO memberVO = memberService.getMemberById(mno);
        return couponMapper.getCouponForGrade(memberVO.getGno());
    }

    @Override
    public void applyCouponToOrder(long mno, long cno) {
        CouponVO couponVO = couponMapper.getCouponById(cno);
        CouponLogVO couponLogVO = new CouponLogVO();
        couponLogVO.setMno(mno);
        couponLogVO.setCno(cno);
        couponLogVO.setStatus("사용 완료");
        couponLogVO.setUsedAt(new Date());
        couponLogVO.setExpAt(couponVO.getExpDay());
        couponLogMapper.insertCouponLog(couponLogVO);

    }
}
