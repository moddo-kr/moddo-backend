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
@RequestMapping("/user")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/guest/token")
    public ResponseEntity<TokenResponse> getGuestToken() {
        return ResponseEntity.ok(authService.createGuestUser());
    }

    @PutMapping("/refresh/token")
    public RefreshResponse refreshToken(@RequestHeader(value = "Authorization") @NotBlank String refreshToken) {
        return refreshTokenService.execute(refreshToken);
    }
}