package com.dnd.moddo.domain.auth.service;

import com.dnd.moddo.domain.auth.exception.TokenInvalidException;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.utill.JwtProvider;
import com.dnd.moddo.global.jwt.utill.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public RefreshResponse execute(String token) {

        String email;

        try {
            email = jwtUtil.getJwt(jwtUtil.parseToken(token)).getBody().get(JwtConstants.EMAIL.message).toString();
        } catch (Exception e) {
            throw new TokenInvalidException();
        }

        User user = userRepository.getByEmail(email);
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getAuthority().toString());

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
