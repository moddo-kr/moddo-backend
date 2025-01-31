package com.dnd.moddo.global.jwt.auth;

import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.utill.JwtUtil;
import com.dnd.moddo.global.security.auth.AuthDetailsService;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuth {
    private final JwtUtil jwtUtil;
    private final AuthDetailsService authDetailsService;

    public Authentication authentication(String token) {
        Claims claims = jwtUtil.getJwt(token).getBody();

        if (isNotAccessToken(token)) {
            throw new IllegalArgumentException("토큰이 입력되지 않았습니다.");
        }

        UserDetails userDetails = authDetailsService.loadUserByUsername(claims.get(JwtConstants.AUTH_ID.message).toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private boolean isNotAccessToken(String token) {

        if (token.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String role = jwtUtil.getJwt(token).getHeader().get(JwtConstants.TYPE.message).toString();
        return !role.equals(JwtConstants.ACCESS_KEY.message);
    }
}
