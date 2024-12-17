package com.ezen.books.repository;

import com.ezen.books.domain.MemberAuth;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.domain.PointsVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface MemberMapper {

    boolean checkLoginIdDuplicate(String loginId);

    void insert(MemberVO memberVO);

    MemberVO findByLoginId(String username);

    void saveTokens(MemberVO memberVO);

    int updateMember(MemberVO memberVO);

    int deleteMember(String loginId);

    int checkUser(@Param("loginId") String loginId, @Param("password") String password);


    double getTotalSpentInLast3Months(long mno);

    void updateMemberGrade(long mno, long gradeNo);

    MemberVO getMemberByInfo(long mno);
}
