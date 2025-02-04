package com.dnd.moddo.global.jwt.auth;

import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.utill.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuth jwtAuth;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);

        if (token != null) {
            String requestURI = request.getRequestURI();
            String expectedTokenType = getExpectedTokenType(requestURI);

            Authentication authentication = jwtAuth.getAuthentication(token, expectedTokenType);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getExpectedTokenType(String requestURI) {
        if (requestURI.startsWith("/api/v1/user/reissue/token")) {
            return JwtConstants.REFRESH_KEY.message;
        }
        return JwtConstants.ACCESS_KEY.message;
    }
}
