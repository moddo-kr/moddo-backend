package com.dnd.moddo.domain.auth.service;

import static com.dnd.moddo.global.support.UserTestFactory.*;
import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.moddo.domain.auth.exception.TokenInvalidException;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.properties.JwtConstants;
import com.dnd.moddo.global.jwt.utill.JwtProvider;
import com.dnd.moddo.global.jwt.utill.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

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

	@BeforeEach
	void setUp() {
	}

	@Test
	public void reissueAccessToken() {
		// given
		String validToken = "validToken";
		String email = "test@example.com";
		Long userId = 1L;
		String role = "USER";
		String newAccessToken = "newAccessToken";

		Jws<Claims> mockJws = mock(Jws.class);
		Claims mockClaims = mock(Claims.class);

		when(jwtUtil.getJwt(any())).thenReturn(mockJws);
		when(mockJws.getBody()).thenReturn(mockClaims);
		when(mockClaims.get(JwtConstants.EMAIL.message)).thenReturn(email);

		User user = createGuestDefault();
		ReflectionTestUtils.setField(user, "id", userId);

		when(userRepository.getByEmail(email)).thenReturn(user);
		when(jwtProvider.generateAccessToken(userId, role)).thenReturn(newAccessToken);

		// when
		RefreshResponse response = refreshTokenService.execute(validToken);

		// then
		then(response.getAccessToken()).isEqualTo(newAccessToken);
		verify(userRepository, times(1)).getByEmail(email);
		verify(jwtProvider, times(1)).generateAccessToken(userId, role);
	}

	@Test
	public void shouldThrowOnInvalidToken() {
		// given
		String invalidToken = "invalidToken";
		when(jwtUtil.getJwt(any())).thenThrow(new JwtException("Invalid token"));

		// when & then
		thenThrownBy(() -> refreshTokenService.execute(invalidToken))
			.isInstanceOf(TokenInvalidException.class);
	}
}
