package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.AddressListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressListServiceImpl implements AddressListService {

    private final AddressListMapper addressListMapper;

    @Override
    public List<AddressVO> getAllAddr(long mno) {
        return addressListMapper.getAllAddr(mno);
    }

    @Override
    public MemberVO getMember(long mno) {
        return addressListMapper.getMember(mno);
    }

    @Override
    public GradeVO getMemberGrade(Long gno) {
        return addressListMapper.getMemberGrade(gno);
    }
}
