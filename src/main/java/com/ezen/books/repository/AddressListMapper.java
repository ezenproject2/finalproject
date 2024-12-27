package com.ezen.books.repository;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressListMapper {

    MemberVO getMember(long mno);

    GradeVO getMemberGrade(Long gno);

    List<AddressVO> getAllAddr(long mno);

    void registerAddr(AddressVO addressData);

    void setAllAddrNotDefault();

    int deleteAddr(long adnoData);
}
