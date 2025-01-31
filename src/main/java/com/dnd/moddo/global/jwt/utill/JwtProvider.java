package com.dnd.moddo.global.jwt.utill;

import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

import static com.dnd.moddo.global.jwt.properties.JwtConstants.*;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public TokenResponse generateGuestToken(Long id, String email) {
        String accessToken = generateToken(id, email, "GUEST", ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
        String refreshToken = generateToken(id, email, "GUEST", REFRESH_KEY.getMessage(), jwtProperties.getRefreshExpiration());

        return new TokenResponse(accessToken, refreshToken, getExpiredTime(), true);
    }

    private String generateToken(Long id, String email, String role, String type, Long exp) {
        return Jwts.builder()
                .claim("userId", id)
                .setHeaderParam("type", type)
                .claim("role", role)
                .claim("email", email)
                .signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + exp * 1000))
                .compact();
    }

    private ZonedDateTime getExpiredTime() {
        return ZonedDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration());
    }
}