package com.ezen.books.handler;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Getter
@Setter
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;

    private String authLoginId;
    private String authUrl;

    // redirect 데이터를 가지고 리다이렉트 경로로 이동하는 역할
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    // 세션의 캐쉬 정보, 직전 URL 경로
    private RequestCache requestCache = new HttpSessionRequestCache();

    public LoginSuccessHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 로그인 후, 사용자의 loginId를 가져와서 MemberVO 객체를 가져옵니다.
        setAuthLoginId(authentication.getName());

        // Service를 통해 로그인한 사용자의 정보를 가져옵니다.
        MemberVO member = memberService.getMemberByInfo(getAuthLoginId());

        if (member == null) {
            return; // 로그인 정보가 없다면, 리턴
        }

        // 2. 로그인 성공 시, 'lastLogin' 갱신
        int isOk = memberService.updateLastLogin(getAuthLoginId());
        setAuthUrl("/");

        HttpSession ses = request.getSession();
        if(isOk == 0 || ses == null){
            return;
        } else {
            ses.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        redirectStrategy.sendRedirect(request, response,
                savedRequest != null ? savedRequest.getRedirectUrl() : getAuthUrl());

    }
}
