package com.ezen.books.service;

import com.ezen.books.domain.UserVO;
import com.ezen.books.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public int insert(UserVO userVO) {
        int isOk = userMapper.insert(userVO);
        long mno = userMapper.getMno();
        if(isOk > 0){
            userMapper.authInsert(mno);
        }
        return isOk;
    }

    @Override
    public int modifyPwdEmpty(UserVO uvo) {
        return userMapper.modifyPwdEmpty(uvo);
    }

    @Override
    public int modify(UserVO uvo) {
        return userMapper.modify(uvo);
    }

    @Override
    public int remove(String email) {
        int isOk = userMapper.removeAuth(email);
        return userMapper.remove(email);
    }

    @Override
    public int updateLastLogin(String name) {
        return userMapper.updateLastLogin(name);
    }
}
