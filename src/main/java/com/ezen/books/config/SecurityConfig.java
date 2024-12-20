package com.ezen.books.config;

import com.ezen.books.handler.LoginFailureHandler;
import com.ezen.books.handler.LoginSuccessHandler;
import com.ezen.books.jwt.JwtAuthorizationFilter;
import com.ezen.books.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ezen.books.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.ezen.books.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.ezen.books.oauth2.service.CustomOAuth2UserService;
import com.ezen.books.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    /* springSecurity6 => bcEncoder 변경 : PasswordEncoder => createDelegatingPasswordEncoder */
    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // SecurityFilterChain 객체로 설정
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // csrf 보호 비활성화
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        // URL 접근 권한 설정
        http.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers("/member/").authenticated()
                    .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // "MAMAGER" -> "MANAGER"
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .requestMatchers("/login", "/member/join").permitAll()
                    .requestMatchers("/mypage/**").authenticated()
                    .anyRequest().permitAll();  // 그 외의 요청은 모두 접근 가능
        });

        // 로그인 설정
        http.formLogin(login -> {
            login
                    .usernameParameter("loginId")  // 로그인 폼에서 아이디는 "id"로 받기
                    .passwordParameter("password")  // 비밀번호는 "pwd"로 받기
                    .loginPage("/member/login")  // 로그인 페이지 경로 설정
                    .defaultSuccessUrl("/")  // 로그인 성공 후 리디렉션할 URL 설정
                    .permitAll()     // 로그인 페이지는 누구나 접근 가능
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler);
        });

        http.oauth2Login(configure -> configure
                .authorizationEndpoint(config ->
                        config.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                .userInfoEndpoint(config ->
                        config.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );

        // 로그아웃 설정
        http.logout(logout -> {
            logout
                    .logoutUrl("/member/logout")  // 로그아웃 URL 설정
                    .invalidateHttpSession(true)  // 로그아웃 후 세션 무효화
                    .deleteCookies("JSESSIONID")  // "JSESSIONID" 쿠키 삭제
                    .logoutSuccessUrl("/");  // 로그아웃 성공 후 리디렉션할 URL 설정
        });

        return http.build();
    }

    // userDetailsService : spring에서 만든 클래스와 같은 객체
    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();     //security 패키지에 클래스로 생성
    }

    // authenticationManager 객체
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
