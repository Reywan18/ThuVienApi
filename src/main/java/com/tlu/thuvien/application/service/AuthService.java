package com.tlu.thuvien.application.service;

import com.tlu.thuvien.api.dto.request.auth.LoginRequest;
import com.tlu.thuvien.api.dto.request.auth.RegisterRequest;
import com.tlu.thuvien.api.dto.response.auth.AuthResponse;
import com.tlu.thuvien.domain.entity.Cart;
import com.tlu.thuvien.domain.entity.User;
import com.tlu.thuvien.infrastructure.repository.CartRepository;
import com.tlu.thuvien.infrastructure.repository.UserRepository;
import com.tlu.thuvien.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        AuthResponse response = new AuthResponse(jwt);

        response.setRole(user.getRole().name());
        return response;
    }

    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        return "Đăng ký thành công!";
    }
}