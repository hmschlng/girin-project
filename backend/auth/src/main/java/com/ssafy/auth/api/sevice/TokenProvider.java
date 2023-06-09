package com.ssafy.auth.api.sevice;

import com.ssafy.auth.api.JwtCode;
import com.ssafy.auth.api.TokenKey;
import com.ssafy.auth.api.dto.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class TokenProvider implements InitializingBean {
    private final String secret;
    private final long tokenValidityInMilliseconds;     // 1 hour
    // private final RedisService redisService;
    private Key key;

    @Autowired
    public TokenProvider(
        @Value("${jwt.secret}")String secret,
        @Value("${jwt.token-validity-in-seconds}")long tokenValidityInSeconds
        // RedisService redisService
    ) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        // this.redisService = redisService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // public String getSavedRefresh(String key) {
    //     return redisService.getData(key);
    // }

    // public void setSaveRefresh(String key, String value, Long time) {
    //     redisService.setDataWithExpiration(key, value, time);
    // }

    public String generateAccess(String userId, String role) {
        return createToken(userId, role, TokenKey.ACCESS);
    }

    public String generateRefresh(String userId, String role) {
        return createToken(userId, role, TokenKey.REFRESH);
    }

    public Token generateToken(String userId, String role) {
        String accessToken = generateAccess(userId, role);
        String refreshToken = generateRefresh(userId, role);

        return new Token(accessToken, refreshToken);
    }

    public String createToken(String userId, String role, TokenKey tokenKey) {
        // access : 1 hour, refresh : 1 month
        long period = getExpiration(tokenKey);

        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + period))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public JwtCode validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            // 만료된 경우에는 refresh token을 확인하기 위해
            return JwtCode.EXPIRED;
        } catch (JwtException | IllegalArgumentException  e) {
            log.info("jwtException = {}", e);
        }
        return JwtCode.DENIED;
    }

/*
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
*/

    public String getUid(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    public Long getExpiration(TokenKey tokenKey) {

        String delimiter = tokenKey.getKey();
        if (delimiter.equals(TokenKey.ACCESS.getKey())) {
            return tokenValidityInMilliseconds;
        } else if (delimiter.equals(TokenKey.REFRESH.getKey())) {
            return tokenValidityInMilliseconds * 2L * 24L * 30;
        } else {
            throw new RuntimeException();
        }
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer-")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}