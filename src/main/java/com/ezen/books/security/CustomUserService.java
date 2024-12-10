package com.ezen.books.security;

import com.ezen.books.domain.UserVO;
import com.ezen.books.repository.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // userName 주고 UserVO 객체 리턴 (authList 같이)
        log.info(">>>>username {}",username);
        UserVO userVO = userMapper.selectId(username);
        log.info(">>> login userVO {}", userVO);
        if (userVO == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        userVO.setAuthList(userMapper.selectAuths(userVO.getMno()));
        // UserDetails return
        return new AuthUser(userVO);
    }
}