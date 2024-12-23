package com.ezen.books.jwt;

import com.ezen.books.oauth2.service.OAuth2UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application-secrets.properties")
public class TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME_IN_MILLISECONDS = 1000 * 60 * 30;    // 30min
    private Key key;
    // 설정 값 주입
    @Value("${spring.jwt.issuer}")
    private String issuer; // 발급자

    @Value("${spring.jwt.secret}")
    private String secretKey; // 비밀 키

    @PostConstruct
    public void init() {
//        byte[] secretkey = "R29vZEFmdGVyb25vbkdvb2RFdmVuaW5nR29vZG5pZ2h0".getBytes();
//        this.key = Keys.hmacShaKeyFor(secretkey);
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    // JWT(Access Token) 생성
    public String createToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();


        if (principal instanceof OAuth2UserPrincipal) {
            OAuth2UserPrincipal oAuth2UserPrincipal = (OAuth2UserPrincipal) principal;
            String username = oAuth2UserPrincipal.getUsername();
            // OAuth2UserPrincipal에서 roles 정보를 가져옵니다.
            List<String> roles = oAuth2UserPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            // JWT의 payload에 'roles' 정보를 추가하고, 만료 시간을 설정하여 생성합니다.
            Date date = new Date();
            Date expiryDate = new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME_IN_MILLISECONDS);

            return Jwts.builder()
                    .setSubject(username)
                    .claim("roles", roles)
                    .setIssuedAt(date)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }

        // 예외 처리: 예상되는 principal 타입이 아닌 경우
        throw new IllegalStateException("Principal type is not OAuth2UserPrincipal");
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException exception) {
            log.error("> JWT is expired: {}", exception.getMessage());
        } catch (SignatureException exception) {
            log.error("> JWT signature validation fails: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("> JWT is malformed: {}", exception.getMessage());
        } catch (Exception exception) {
            log.error("> JWT validation fails: {}", exception.getMessage());
        }
        return false;
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        // JWT 토큰에서 사용자 정보를 가져와서 Authentication 객체를 생성
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // "roles" 클레임을 가져오기
        List<String> roles = claims.get("roles", List.class); // JWT에서 roles가 List<String> 형태로 저장되었다고 가정
        // roles 정보를 GrantedAuthority로 변환
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))  // 역할을 권한으로 변환
                .collect(Collectors.toList());


        // 권한이 포함된 사용자 객체 생성 (비밀번호는 null로 설정)
        String username = claims.getSubject();  // JWT에서 username (또는 email) 가져오기
        // 인증 객체 생성 (UserDetails는 비밀번호가 필요없으므로 빈 문자열로 설정)
        UserDetails user = new User(username, "", authorities); // 비밀번호를 공백이 아닌 null로 설정하는 것이 안전함

        // 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }



}
