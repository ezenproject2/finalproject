package com.ezen.books.service;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.domain.OrdersVO;
import jakarta.validation.constraints.NotBlank;

public interface MemberService {
    
    boolean checkLoginIdDuplicate(@NotBlank(message = "ID를 입력하세요.") String loginId);

    void insert(MemberVO memberVO);

    String getExistingPassword(@NotBlank(message = "ID를 입력하세요.") String loginId);

    int updateMember(MemberVO memberVO);

    int deleteMember(String loginId);

    /*--------------------------------------*/
    // 회원 등급 갱신
    void updateMemberGrade(long mno);

    // 회원 정보 페이지
    MemberVO getMemberByInfo(long mno);

    // 회원 등급에 따른 비율 반환
    double getPointRateByGrade(long mno);

    MemberVO getMemberById(long mno);

}
