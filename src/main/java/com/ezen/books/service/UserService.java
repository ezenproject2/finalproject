package com.ezen.books.service;

import com.ezen.books.domain.UserVO;

public interface UserService {
    int insert(UserVO userVO);

//    List<UserVO> getList();

    int modifyPwdEmpty(UserVO uvo);

    int modify(UserVO uvo);

    int remove(String email);

    int updateLastLogin(String name);
}