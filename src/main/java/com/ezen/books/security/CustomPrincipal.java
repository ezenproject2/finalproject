package com.ezen.books.security;

import com.ezen.books.domain.MemberVO;

public interface CustomPrincipal {
    MemberVO getMemberVO();
}
