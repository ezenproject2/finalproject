package com.ezen.books.config;

import com.ezen.books.security.CustomUserService;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
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
            authorize.requestMatchers("/user/").authenticated()
                    .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // "MAMAGER" -> "MANAGER"
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .requestMatchers("/login").permitAll()  // "/login"은 누구나 접근 가능하도록 설정
                    .requestMatchers("/user/join").permitAll() // 예시로 추가된 회원가입 페이지 접근 설정
                    .anyRequest().permitAll();  // 그 외의 요청은 모두 접근 가능
        });

        // 로그인 설정
        http.formLogin(login -> {
            login
                    .usernameParameter("id")  // 로그인 폼에서 아이디는 "id"로 받기
                    .passwordParameter("pwd")  // 비밀번호는 "pwd"로 받기
                    .loginPage("/user/login")  // 로그인 페이지 경로 설정
                    .defaultSuccessUrl("/")  // 로그인 성공 후 리디렉션할 URL 설정
                    .permitAll();  // 로그인 페이지는 누구나 접근 가능
        });

        // 로그아웃 설정
        http.logout(logout -> {
            logout
                    .logoutUrl("/user/logout")  // 로그아웃 URL 설정
                    .invalidateHttpSession(true)  // 로그아웃 후 세션 무효화
                    .deleteCookies("JSESSIONID")  // "JSESSIONID" 쿠키 삭제
                    .logoutSuccessUrl("/");  // 로그아웃 성공 후 리디렉션할 URL 설정
        });

        return http.build();
    }
    // userDetailsService : spring에서 만든 클래스와 같은 객체
    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserService();     //security 패키지에 클래스로 생성
    }

    // authenticationManager 객체
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}