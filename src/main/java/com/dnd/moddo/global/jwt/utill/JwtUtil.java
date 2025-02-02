package com.dnd.moddo.global.jwt.utill;

import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(jwtProperties.getHeader());
        return parseToken(bearer);
    }

    public String parseToken(String bearer) {
        if (bearer != null && bearer.startsWith(jwtProperties.getPrefix())) {
            return bearer.replace(jwtProperties.getPrefix(), "").trim();
        }
        return null;
    }

    public Jws<Claims> getJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build().parseClaimsJws(token);
    }

    public boolean isRefreshToken(String token) {
        return token != null && getJwt(token).getHeader().get(JwtConstants.TYPE.message).toString()
                .equals(JwtConstants.REFRESH_KEY.message);
    }

}