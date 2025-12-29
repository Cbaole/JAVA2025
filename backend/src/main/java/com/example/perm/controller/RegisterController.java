package com.example.perm.controller;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AuthDtos;
import com.example.perm.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/check-idcard")
    public ApiResponse<AuthDtos.RegisterCheckResponse> check(@RequestParam String idCard) {
        return ApiResponse.ok(registerService.checkIdCard(idCard));
    }

    @PostMapping
    public ApiResponse<String> submit(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        return ApiResponse.ok(registerService.register(req));
    }
}

