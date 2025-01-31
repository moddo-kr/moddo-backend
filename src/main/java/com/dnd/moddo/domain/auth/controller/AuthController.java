package com.dnd.moddo.domain.auth.controller;

import com.dnd.moddo.domain.auth.service.AuthService;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/guest/token")
    public ResponseEntity<TokenResponse> getGuestToken() {
        return ResponseEntity.ok(authService.createGuestUser());
    }
}