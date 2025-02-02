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

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public RefreshResponse execute(String token) {

        String email;

        try {
            email = jwtUtil.getJwt(jwtUtil.parseToken(token)).getBody().get(JwtConstants.AUTH_ID.message).toString();
        } catch (Exception e) {
            throw new TokenInvalidException();
        }

        Optional<User> user = userRepository.findByEmail(email);
        String newAccessToken = jwtProvider.generateAccessToken(user.get().getId(), user.get().getEmail(), user.get().getAuthority().toString());

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
