package com.ezen.books.service;

import com.ezen.books.domain.CouponLogVO;
import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.CouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService{

    private final MemberService memberService;
    private final CouponMapper couponMapper;

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

    @Override
    public List<CouponLogVO> getExpiringCouponsThisMonth(long mno) {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String firstDay = firstDayOfMonth.format(formatter);
        String lastDay = lastDayOfMonth.format(formatter);

        return couponMapper.getExpiringCouponsThisMonth(mno, firstDay, lastDay);
    }

}
