package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.request.auth.LoginRequest;
import com.tlu.thuvien.api.dto.request.auth.RegisterRequest;
import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.api.dto.response.auth.AuthResponse;
import com.tlu.thuvien.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse token = authService.login(request);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Đăng nhập thành công",
                token
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Đăng ký thành công",
                null
        ));
    }
}