package com.dnd.moddo.global.jwt.utill;

import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
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

    public String generateAccessToken(Long id, String email, String role) {
        return generateToken(id, email, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
    }

    public TokenResponse generateToken(Long id, String email, String role, Boolean isMember) {
        String accessToken = generateToken(id, email, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
        String refreshToken = generateToken(id, email, role, REFRESH_KEY.getMessage(), jwtProperties.getRefreshExpiration());

        return new TokenResponse(accessToken, refreshToken, getExpiredTime(), isMember);
    }

    public GroupTokenResponse generateGroupToken(Long groupId) {
        String accessToken = generateGroupToken(groupId, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
        String refreshToken = generateGroupToken(groupId, REFRESH_KEY.getMessage(), jwtProperties.getRefreshExpiration());

        return new GroupTokenResponse(accessToken, refreshToken, getExpiredTime());
    }


    private String generateToken(Long id, String email, String role, String type, Long exp) {
        return Jwts.builder()
                .claim(AUTH_ID.getMessage(), id)
                .claim(EMAIL.getMessage(), email)
                .setHeaderParam(TYPE.message, type)
                .claim(ROLE.getMessage(), role)
                .signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
                .setExpiration(
                        new Date(System.currentTimeMillis() + exp * 1000)
                )
                .compact();
    }

    private String generateGroupToken(Long groupId, String type, Long exp) {
        return Jwts.builder()
                .claim(GROUP_ID.getMessage(), groupId)
                .setHeaderParam(TYPE.message, type)
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