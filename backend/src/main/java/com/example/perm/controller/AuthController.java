package com.example.perm.controller;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AuthDtos;
import com.example.perm.security.AuthUserPrincipal;
import com.example.perm.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        return ApiResponse.ok(authService.login(req.username(), req.password()));
    }

    @PostMapping("/me")
    public ApiResponse<Object> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserPrincipal principal)) {
            return ApiResponse.fail("未登录");
        }
        return ApiResponse.ok(new AuthDtos.UserProfile(principal.getUserId(), null, null, principal.getUsername(), principal.getRoleId(), principal.getRoleName()));
    }
}

