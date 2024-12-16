package com.ezen.books.service;

import com.ezen.books.domain.MemberVO;
import jakarta.validation.constraints.NotBlank;

public interface MemberService {
    
    boolean checkLoginIdDuplicate(@NotBlank(message = "ID를 입력하세요.") String loginId);

    void insert(MemberVO memberVO);

    int updateMember(MemberVO memberVO);

    int deleteMember(String loginId);

    boolean validateUser(String loginId, String password);
}
