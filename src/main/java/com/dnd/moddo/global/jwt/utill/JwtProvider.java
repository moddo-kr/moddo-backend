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

    public String generateAccessToken(Long id, String authId, String role) {
        return generateToken(id, authId, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
    }

    public TokenResponse generateToken(Long id, String authId, String role, Boolean isMember) {
        String accessToken = generateToken(id, authId, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
        String refreshToken = generateToken(id, authId, role, REFRESH_KEY.getMessage(), jwtProperties.getRefreshExpiration());

        return new TokenResponse(accessToken, refreshToken, getExpiredTime(), isMember);
    }


    private String generateToken(Long id, String authId, String role, String type, Long exp) {
        return Jwts.builder()
                .claim("userId", id)
                .setHeaderParam(TYPE.message, type)
                .claim(ROLE.getMessage(), role)
                .claim(AUTH_ID.getMessage(), authId)
                .signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
                .setExpiration(
                        new Date(System.currentTimeMillis() + exp * 1000)
                )
                .compact();
    }

    private ZonedDateTime getExpiredTime() {
        return ZonedDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration());
    }
}