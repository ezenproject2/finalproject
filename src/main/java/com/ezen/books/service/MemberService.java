package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.MemberVO;
import jakarta.validation.constraints.NotBlank;

public interface MemberService {
    
    boolean checkLoginIdDuplicate(@NotBlank(message = "ID를 입력하세요.") String loginId);

    void insert(MemberVO memberVO);

    String getExistingPassword(@NotBlank(message = "ID를 입력하세요.") String loginId);

    int updateMember(MemberVO memberVO);

    int deleteMember(String loginId);

    /*--------------------------------------*/
    // 회원 등급 업데이트
    void updateMemberGrade(long mno);

    // 사용자 정보 조회
    MemberVO getMemberByInfo(String loginId);

    // 회원 등급에 맞는 포인트 비율 조회
    double getPointRateByGrade(long mno);

    MemberVO getMemberById(long mno);

    int updateLastLogin(String authLoginId);

    long getLastMno();

    int saveAddressToServer(AddressVO addressVO);
}
