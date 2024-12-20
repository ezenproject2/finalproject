package com.ezen.books.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@Getter
@Setter
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private String authLoginId; // 로그인 시도한 사용자 ID
    private String errorMessage;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // 로그인 시도한 사용자의 loginId 세팅
        setAuthLoginId(request.getParameter("loginId"));

        if(exception instanceof DisabledException){
            setErrorMessage("아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
        } else if(exception instanceof BadCredentialsException){
            setErrorMessage("아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
        } else {
            setErrorMessage("로그인 실패했습니다. 관리자에게 문의해주세요.");
        }

        log.info(">> err_msg : {}", getErrorMessage());
//
//        request.setAttribute("loginId", getAuthLoginId());
//        request.setAttribute("err_msg", getErrorMessage());
//
//        request.getRequestDispatcher("/member/login?error").forward(request, response);
        // URL 파라미터로 오류 메시지와 로그인 ID 전달
        String redirectUrl = "/member/login?error=true&loginId=" + getAuthLoginId() + "&err_msg=" + URLEncoder.encode(getErrorMessage(), "UTF-8");
        response.sendRedirect(redirectUrl);
    }

}
