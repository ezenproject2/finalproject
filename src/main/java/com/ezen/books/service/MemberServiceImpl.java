package com.ezen.books.service;

import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
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

    @Override
    public boolean checkLoginIdDuplicate(String loginId) {
        return memberMapper.checkLoginIdDuplicate(loginId);
    }

    @Override
    public void insert(MemberVO memberVO) {
        memberMapper.insert(memberVO);
    }

    @Override
    public int updateMember(MemberVO memberVO) {
        return memberMapper.updateMember(memberVO);
    }

    @Override
    public int deleteMember(String loginId) {
        return memberMapper.deleteMember(loginId);
    }

    @Override
    public boolean validateUser(String loginId, String password) {
        return memberMapper.checkUser(loginId, password) > 0;
    }

    @Override
    public MemberVO getMemberById(long mno) {
        return memberMapper.getMemberById(mno);
    }

    @Override
    public void updateMemberGrade(long mno) {
        MemberVO memberVO = memberMapper.getMemberById(mno);
        int totalSpent = getTotalSpent(memberVO); // Total spent from orders

        if(totalSpent >= 300000){
            memberVO.setGno(4l);    // 플래티넘
        } else if(totalSpent >= 200000){
            memberVO.setGno(3l);    // 골드
        } else if(totalSpent >= 100000){
            memberVO.setGno(2l);    // 실버
        } else {
            memberVO.setGno(1l);    // 새싹
        }
        memberMapper.updateGrade(mno, memberVO.getGno());
    }

    private int getTotalSpent(MemberVO memberVO) {
        // 회원이 총 지출을 받는 논리 예제:
        return 1500000;
    }

    @Override
    public double calculatePoint(MemberVO memberVO, double bookPrice) {
        GradeVO gradeVO = gradeMapper.getGradeById(memberVO.getGno());
        double pointRate = gradeVO.getPointRate();
        return bookPrice * (pointRate / 100);
    }

}
