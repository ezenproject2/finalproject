package com.ezen.books.oauth2.service;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.ezen.books.oauth2.user.OAuth2UserInfo;
import com.ezen.books.oauth2.user.OAuth2UserInfoFactory;
import com.ezen.books.repository.MemberMapper;
import com.ezen.books.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest){
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try{
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex){
            // 인증 인스턴스 던지기예외가 발생하면 OAuth2AuthenticationFailureHandler가 트리거됩니다
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // OAuth2UserInfo 추출
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, accessToken, oAuth2User.getAttributes());

        // OAuth2UserInfo 유효성 검사
        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())){
            throw new OAuth2AuthenticationProcessingException("Email not found OAuth2 provider");
        }

        MemberVO memberVO = memberMapper.findById(oAuth2UserInfo.getId());

        List<String> roles = List.of("ROLE_USER");

        return new OAuth2UserPrincipal(oAuth2UserInfo, memberVO, roles);
    }


}
