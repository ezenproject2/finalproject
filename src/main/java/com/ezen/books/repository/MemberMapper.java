package com.ezen.books.repository;

import com.ezen.books.domain.MemberAuth;
import com.ezen.books.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    boolean checkLoginIdDuplicate(String loginId);

    void insert(MemberVO memberVO);

    MemberVO findByLoginId(String username);

}
