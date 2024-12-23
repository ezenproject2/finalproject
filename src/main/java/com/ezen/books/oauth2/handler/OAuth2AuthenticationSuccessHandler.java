package com.ezen.books.oauth2.handler;

import com.ezen.books.domain.MemberAuth;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.jwt.TokenProvider;
import com.ezen.books.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ezen.books.oauth2.service.OAuth2UserPrincipal;
import com.ezen.books.oauth2.user.OAuth2UserUnlinkManager;
import com.ezen.books.oauth2.util.CookieUtils;
import com.ezen.books.repository.MemberMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.ezen.books.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;
import static com.ezen.books.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    private final TokenProvider tokenProvider;
    private final MemberMapper memberMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        if (authentication == null) {
            log.error("Authentication is null in onAuthenticationSuccess.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed.");
            return;
        }

        String targetUrl = determineTargetUrl(request, response, authentication);

        if(response.isCommitted()){
            logger.debug("Response has already been committed. Unable to redirect to "+ targetUrl);
            return;
        }

        // 인증된 사용자 정보를 기반으로 JWT 토큰 생성
        String jwtToken = tokenProvider.createToken(authentication);

        // JWT 토큰을 HTTP 응답 헤더에 "Authorization: Bearer <jwtToken>" 형식으로 추가
        response.addHeader("Authorization", "Bearer " + jwtToken);

        // ----- last_login 갱신
        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if(principal != null){
            String loginId = principal.getUserInfo().getProvider() + "_" + principal.getUserInfo().getId();

            memberMapper.updateLastLogin(loginId);
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication){

        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if(principal == null){
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        if("login".equalsIgnoreCase(mode)){
            // 소셜 로그인 시, 이미 존재하는 사용자인지 확인
            String email = principal.getUserInfo().getEmail();
            String name = principal.getUserInfo().getName();
            String nickName = principal.getUserInfo().getNickname();
            String provider = principal.getUserInfo().getProvider().name();
            String providerId = principal.getUserInfo().getId();
            String loginId = provider + "_" + providerId;

//            // 이미 존재하는 사용자인지 확인
//            MemberVO memberVO = memberMapper.findByLoginId(loginId);
            log.info("Searching for loginId: {}", loginId);
            MemberVO memberVO = memberMapper.findByLoginId(loginId);
//            log.info("Found memberVO: {}", memberVO);
//            log.info("Found memberVO.mno: {}", memberVO.getMno());

            if (memberVO != null) {
                // 이미 존재하는 사용자인 경우, DB에 저장하지 않고 바로 리디렉션
                String accessToken = tokenProvider.createToken(authentication);
                String refreshToken = UUID.randomUUID().toString();
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("access_token", accessToken)
                        .queryParam("refresh_token", refreshToken)
                        .build().toUriString();
            } else {
                // 새 사용자일 경우에만 DB에 저장
                String accessToken = tokenProvider.createToken(authentication);
                String refreshToken = UUID.randomUUID().toString();

                // DB에 사용자 정보 저장
                memberVO = new MemberVO();
                memberVO.setLoginId(loginId);
                memberVO.setEmail(email);
                memberVO.setName(name);
                memberVO.setNickName(nickName);
                memberVO.setAccessToken(accessToken);
                memberVO.setRefreshToken(refreshToken);
                memberVO.setProvider(provider);
                memberVO.setProviderId(providerId);
                memberVO.setAuth(MemberAuth.ROLE_USER);  // 기본적으로 USER 권한 부여


                log.info(">> Saving new member: {}", memberVO);
                // DB에 저장
                memberMapper.saveTokens(memberVO);

                // 리디렉션 URL에 토큰 포함
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("access_token", accessToken)
                        .queryParam("refresh_token", refreshToken)
                        .build().toUriString();

            }

        } else if ("unlink".equalsIgnoreCase(mode)) {
            String accessToken = principal.getUserInfo().getAccessToken();

            // DB에서 토큰 삭제
            oAuth2UserUnlinkManager.unlink(principal.getUserInfo().getProvider(), accessToken);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication){
        Object principal = authentication.getPrincipal();

        if(principal instanceof OAuth2UserPrincipal){
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
