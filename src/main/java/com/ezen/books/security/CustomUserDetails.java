package com.ezen.books.security;

import com.ezen.books.domain.MemberVO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails extends User {
    private MemberVO memberVO;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUserDetails(MemberVO memberVO){
        super(memberVO.getLoginId(), memberVO.getPassword(),
                List.of(new SimpleGrantedAuthority(memberVO.getAuth().name()))
        );
        this.memberVO = memberVO;

    }
}
