package com.ezen.books.service;

import com.ezen.books.domain.*;
import com.ezen.books.repository.CouponMapper;
import com.ezen.books.repository.GradeMapper;
import com.ezen.books.repository.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;

    @Override
    public boolean checkLoginIdDuplicate(String loginId) {
        return memberMapper.checkLoginIdDuplicate(loginId);
    }

    @Override
    public void insert(MemberVO memberVO) {
        memberMapper.insert(memberVO);
    }

    @Override
    public String getExistingPassword(String loginId) {
        return memberMapper.existingPassword(loginId);
    }

    @Override
    public int updateMember(MemberVO memberVO) {
        return memberMapper.updateMember(memberVO);
    }

    @Override
    public int deleteMember(String loginId) {
        return memberMapper.deleteMember(loginId);
    }

    /*---------------------------------------*/

    @Override
    public MemberVO getMemberByInfo(String loginId) {
        return memberMapper.getMemberByInfo(loginId);
    }

    public void updateAllMemberGrades() {
        List<MemberVO> members = memberMapper.getAllMembers();  // 모든 회원을 가져옴

        for (MemberVO member : members) {
            long mno = member.getMno();  // MemberVO에서 mno 값을 추출
            updateMemberGrade(mno);  // 해당 회원의 등급 갱신
        }
    }

    @Override
    public void updateMemberGrade(long mno) {
        // 3개월 이내 총 구매 금액 가져오기
        Double totalSpent = memberMapper.getTotalSpentInLast3Months(mno);
        log.info(">>> Total Spent for mno= " + mno + ": " + totalSpent);

        // 만약 totalSpent가 null이면 0으로 간주
        if (totalSpent == null) {
            totalSpent = 0.0;
        }

        // 구매 금액에 따라 등급 계산
        long gno = calculateGrade(totalSpent);
        log.info("Calculated Grade for mno=" + mno + ": " + gno);

        try {
            // 등급 업데이트
            memberMapper.updateMemberGrade(mno, gno);
            log.info("Successfully updated grade for mno=" + mno + " to gno=" + gno);

            expireOldCoupons(mno);  // 기존 쿠폰 만료 처리
            giveCouponToMember(mno, gno);  // 새로운 등급에 맞는 쿠폰 지급

        } catch (Exception e) {
            log.error("회원 등급 갱신 중 오류 발생: mno=" + mno + ", Error: " + e.getMessage(), e);
        }

    }
    private long calculateGrade(double totalSpent) {
        if (totalSpent >= 300000) {
            return 4;  // 플래티넘
        } else if (totalSpent >= 200000) {
            return 3;  // 골드
        } else if (totalSpent >= 100000) {
            return 2;  // 실버
        } else {
            return 1;  // 새싹
        }
    }

    private void giveCouponToMember(long mno, long gno) {
        try{
            // 해당 등급에 맞는 쿠폰 조회
            List<CouponVO> coupons = memberMapper.getCouponsForGrade(gno);

            if(coupons != null && !coupons.isEmpty()){
                // 쿠폰 리스트에서 하나씩 쿠폰 지급
                for(CouponVO coupon : coupons){
                    CouponLogVO couponLogVO = new CouponLogVO();
                    couponLogVO.setMno(mno);
                    couponLogVO.setCno(coupon.getCno());
                    couponLogVO.setStatus("사용 가능");
                    couponLogVO.setUsedAt(null);
                    couponLogVO.setTitle(coupon.getTitle());
                    couponLogVO.setExpAt(calculateExpirationDate(3));  // 만료 날짜는 지급 날짜로부터 3개월 후

                    memberMapper.insertCouponLog(couponLogVO);  // 쿠폰 로그에 추가
                    log.info("쿠폰 지급 완료: mno=" + mno + ", coupon=" + coupon.getCno());
                }
            }
        } catch (Exception e) {
            log.error("쿠폰 지급 중 오류 발생: mno=" + mno + ", Error: " + e.getMessage(), e);
        }
    }

    // 만료일 계산: 기본 3개월 후로 설정
    private Date calculateExpirationDate(int monthsToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthsToAdd);
        return calendar.getTime();
    }
    // 기존 쿠폰 만료 처리
    private void expireOldCoupons(long mno){
        try{
            memberMapper.updateCouponStatusToExpired(mno);
            log.info("기존 쿠폰 만료 처리 완료: mno = "+mno);
        } catch (Exception e){
            log.error("기존 쿠폰 만료 처리 중 오류 발생: mno=" + mno + ", Error: " + e.getMessage(), e);
        }
    }


    @Override
    public double getPointRateByGrade(long mno) {
        return 0;
    }

    @Override
    public MemberVO getMemberById(long mno) {
        return memberMapper.getMemberById(mno);
    }

    @Override
    public int updateLastLogin(String authLoginId) {
        return memberMapper.updateLastLogin(authLoginId);
    }

    @Override
    public long getMno(String memberLoginId) {
        return memberMapper.getMno(memberLoginId);
    }

    @Override
    public int saveAddressToServer(AddressVO addressVO) {
        addressVO.setAddrName("기본 배송지");
        addressVO.setIsDefault("Y");
        return memberMapper.saveAddressToServer(addressVO);
    }

    /* ---이메일 인증---*/
    @Override
    public MemberVO selectMember(String email, String name) {
        return memberMapper.selectMember(email, name);
    }

    @Override
    public MemberVO selectMemberByEmail(String email) {
        return memberMapper.selectMemberByEmail(email);
    }

    @Override
    public int pwUpdate(MemberVO memberVO) {
        return memberMapper.pwUpdate(memberVO);
    }
    /* --------------*/

    @Override
    public List<MemberVO> getList() {
        return memberMapper.getList();
    }

    @Override
    public List<OrderDetailProductDTO> getRecentBooks(long mno) {
        return memberMapper.getRecentBooks(mno);
    }


}
