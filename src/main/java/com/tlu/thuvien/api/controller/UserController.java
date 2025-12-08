package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.domain.entity.User;
import com.tlu.thuvien.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/my-qr")
    public ResponseEntity<ApiResponse<Map<String, String>>> getMyQrContent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String qrContent = "USER:" + user.getId() + ";DATA=thuvien_secure";
        // String qrContent = "ID#" + user.getId();

        Map<String, String> result = new HashMap<>();
        result.put("qrContent", qrContent);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy mã định danh QR thành công",
                result
        ));
    }
}
