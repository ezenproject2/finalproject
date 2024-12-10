package com.ezen.books.repository;

import com.ezen.books.domain.AuthVO;
import com.ezen.books.domain.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(UserVO userVO);

    long getMno();

    void authInsert(long mno);

    UserVO selectId(String username);

    List<AuthVO> selectAuths(long mno);

    List<UserVO> getList();

    int modifyPwdEmpty(UserVO uvo);

    int modify(UserVO uvo);

    int removeAuth(String email);

    int remove(String email);

    int updateLastLogin(String name);

}
