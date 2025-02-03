package com.dnd.moddo.domain.auth.controller;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.domain.auth.service.RefreshTokenService;
import com.dnd.moddo.global.jwt.dto.RefreshResponse;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/guest/token")
    public ResponseEntity<TokenResponse> getGuestToken() {
        return ResponseEntity.ok(authService.createGuestUser());
    }

    @PutMapping("/reissue/token")
    public RefreshResponse reissueAccessToken(@RequestHeader(value = "Authorization") @NotBlank String refreshToken) {
        return refreshTokenService.execute(refreshToken);
    }
}