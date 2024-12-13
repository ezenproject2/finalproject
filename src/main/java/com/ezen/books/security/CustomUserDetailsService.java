package com.ezen.books.security;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // userName 주고 UserVO 객체 리턴 (authList 같이)
        log.info(">>> username : {}",username);
        MemberVO memberVO = memberMapper.findByLoginId(username);
        log.info(">>> login userVO {}", memberVO);
        if (memberVO == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // UserDetails return
        return new CustomUserDetails(memberVO);
    }
}
