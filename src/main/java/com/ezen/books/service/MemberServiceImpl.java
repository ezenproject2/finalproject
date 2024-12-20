package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
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
    public long getLastMno() {
        return memberMapper.getLastMno();
    }

    @Override
    public int saveAddressToServer(AddressVO addressVO) {
        addressVO.setAddrName("기본 배송지");
        addressVO.setIsDefault("Y");
        return memberMapper.saveAddressToServer(addressVO);
    }


}
