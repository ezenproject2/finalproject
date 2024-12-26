package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;

import java.util.List;

public interface AddressListService {
    List<AddressVO> getAllAddr(long mno);

    MemberVO getMember(long mno);

    GradeVO getMemberGrade(Long gno);
}
