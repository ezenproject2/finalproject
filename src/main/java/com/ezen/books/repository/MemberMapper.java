package com.ezen.books.repository;

import com.ezen.books.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    boolean checkLoginIdDuplicate(String loginId);

    void insert(MemberVO memberVO);

    MemberVO findByLoginId(String username);

    void saveTokens(MemberVO memberVO);

    String existingPassword(String loginId);

    int updateMember(MemberVO memberVO);

    int deleteMember(String loginId);

    Double getTotalSpentInLast3Months(long mno);

    void updateMemberGrade(@Param("mno") long mno, @Param("gno") long gno);

    MemberVO getMemberByInfo(String loginId);

    MemberVO getMemberById(long mno);

    MemberVO findById(String id);

    int updateLastLogin(String authLoginId);

    long getLastMno();

    int saveAddressToServer(AddressVO addressVO);

    List<MemberVO> getAllMembers();

    List<CouponVO> getCouponsForGrade(long gno);

    void insertCouponLog(CouponLogVO couponLogVO);

    void updateCouponStatusToExpired(long mno);
}
