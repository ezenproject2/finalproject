package com.ezen.books.service;

import com.ezen.books.domain.*;
import com.ezen.books.repository.CouponMapper;
import com.ezen.books.repository.GradeMapper;
import com.ezen.books.repository.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;
    private final GradeMapper gradeMapper;
    private final CouponMapper couponMapper;

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

    @Override
    public void updateMemberGrade(long mno) {
        // 3개월 이내의 구매 금액 합계
        double totalSpent = memberMapper.getTotalSpentInLast3Months(mno);

        // 구매 금액에 따른 금액 갱신
        long gno = calculateGrade(totalSpent);

        // 등급 업데이트
        memberMapper.updateMemberGrade(mno, gno);
    }

    private long calculateGrade(double totalSpent){
        if(totalSpent >= 300000){
            return 4;    // 플래티넘
        } else if(totalSpent >= 200000){
            return 3;    // 골드
        } else if(totalSpent >= 100000){
            return 2;    // 실버
        } else {
            return 1;    // 새싹
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


}
