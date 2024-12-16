package com.ezen.books.oauth2.service;

import com.ezen.books.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.ezen.books.oauth2.user.OAuth2UserInfo;
import com.ezen.books.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

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

        // 사용자에게 기본 역할 추가 (예: "ROLE_USER")
        List<String> roles = List.of("ROLE_USER");  // 기본적인 역할 할당 (추가적인 역할은 상황에 맞게 설정 가능)

        return new OAuth2UserPrincipal(oAuth2UserInfo, roles);
    }


}
