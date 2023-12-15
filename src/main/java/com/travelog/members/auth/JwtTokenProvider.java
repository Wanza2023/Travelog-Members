package com.travelog.members.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CustomUserDetailService userDetailService;

    private String secretKey = "secretKey";

    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(String userPk) {  // userPK = email

        long tokenValidTime = 1000L * 60 * 60 * 3;  // 토큰 유효 시간 3시간
        Date now = new Date();  // 토큰 발행 시간 정보
        Date exp = new Date(now.getTime() + tokenValidTime);  // 토큰 유효 시간 설정

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userPk)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 유효성, 만료일자 확인
    public boolean isTokenValid(String jwtToken) {
        try {
            Date now = new Date();
            Date exp = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getExpiration();
            return now.before(exp);
        } catch (Exception e) {
            log.info("error={}", e.getMessage());
            return false;
        }
    }

    public String getAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getToken(String header) {
        if (header == null) throw new IllegalArgumentException("헤더에 Authorization이 없습니다.");
        if (!header.startsWith("Bearer")) throw new IllegalArgumentException("토큰이 Bearer로 시작하지 않습니다.");

        return header.substring("Bearer ".length());
    }
}
