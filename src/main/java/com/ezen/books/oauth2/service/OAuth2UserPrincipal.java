package com.ezen.books.oauth2.service;

import com.ezen.books.oauth2.user.OAuth2UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private final OAuth2UserInfo userInfo;
    private final List<String> roles;

    public OAuth2UserPrincipal(OAuth2UserInfo userInfo, List<String> roles) {
        this.userInfo = userInfo;
        this.roles = roles;
    }


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userInfo.getEmail();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return userInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        if(roles != null){
            for(String role : roles){
                authorityList.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorityList;
    }

    @Override
    public String getName() {
        return userInfo.getName();
    }

    // UserDetails 메서드들
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 여부 (유효)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠금 여부 (유효)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명 만료 여부 (유효)
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정 활성화 여부 (유효)
    }

    public OAuth2UserInfo getUserInfo() {
        return userInfo;  // OAuth2 사용자 정보 반환
    }

}
