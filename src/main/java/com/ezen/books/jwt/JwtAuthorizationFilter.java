package com.ezen.books.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 로그인 또는 OAuth2 인증 요청에 대한 JWT 인증 건너뛰기
        if (request.getRequestURI().equals("/login") || request.getRequestURI().equals("/oauth2/authorization/*")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request); // 헤더에서 토큰을 추출

        // 토큰이 존재하고 유효한지 검증
        if (token != null && tokenProvider.validateToken(token)) {

            // 토큰에서 인증 정보를 추출하여 SecurityContext에 설정
            Authentication authentication = tokenProvider.getAuthentication(token);

            // 로그로 인증 객체가 제대로 생성되는지 확인
            if(authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info(">>>>>>> Authentication set : {}", authentication.getName());
            } else {
                log.info(">>>>>>>> Authentication is null");
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ANONYMOUS"))) {
            authentication.getAuthorities().forEach(GrantedAuthority::getAuthority);
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }
}
