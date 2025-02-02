package com.dnd.moddo.domain.auth.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.utill.JwtProvider;
import com.dnd.moddo.global.jwt.utill.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    public void executeNewAccessToken() {
        // given
        String refreshToken = "RefreshToken";
        String email = "test@example.com";
        LocalDateTime time = LocalDateTime.now();
        User user = new User(1L, "김철수", email, "profile.png", true, time, time.plusMonths(1), Authority.USER);

        Claims mockClaims = mock(Claims.class);
        Jws<Claims> mockJws = mock(Jws.class);

        when(mockClaims.get(JwtConstants.AUTH_ID.message)).thenReturn(email);
        when(mockJws.getBody()).thenReturn(mockClaims);

        when(jwtUtil.parseToken(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.getJwt(refreshToken)).thenReturn(mockJws);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String newAccessToken = "newAccessToken";
        when(jwtProvider.generateAccessToken(anyLong(), anyString(), anyString())).thenReturn(newAccessToken);

        // when
        RefreshResponse response = refreshTokenService.execute(refreshToken);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);

        verify(jwtUtil, times(1)).parseToken(refreshToken);
        verify(jwtUtil, times(1)).getJwt(refreshToken);
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtProvider, times(1)).generateAccessToken(anyLong(), anyString(), anyString());
    }
}
