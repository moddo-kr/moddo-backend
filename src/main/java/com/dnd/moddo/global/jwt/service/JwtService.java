package com.dnd.moddo.global.jwt.service;

import com.dnd.moddo.global.jwt.utill.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;

    public Long getUserId(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        return jwtUtil.getUserIdFromToken(token);
    }
}
