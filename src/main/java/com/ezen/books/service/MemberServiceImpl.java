package com.ezen.books.service;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
