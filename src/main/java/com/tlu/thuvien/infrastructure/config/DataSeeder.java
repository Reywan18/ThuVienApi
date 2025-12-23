package com.tlu.thuvien.infrastructure.config;

import com.tlu.thuvien.domain.entity.Cart;
import com.tlu.thuvien.domain.entity.User;
import com.tlu.thuvien.domain.entity.UserRole;
import com.tlu.thuvien.infrastructure.repository.CartRepository;
import com.tlu.thuvien.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem email admin đã tồn tại chưa
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            // 1. Tạo tài khoản Admin
            User admin = User.builder()
                    .name("Super Administrator")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456")) // Mật khẩu mặc định
                    .role(UserRole.ADMIN)
                    .build();

            User savedAdmin = userRepository.save(admin);

            // 2. Tạo luôn giỏ hàng cho Admin (để tránh lỗi nếu Admin thử mượn sách)
            Cart cart = Cart.builder().user(savedAdmin).build();
            cartRepository.save(cart);

            System.out.println("---------------------------------------------");
            System.out.println(">>> ĐÃ KHỞI TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH <<<");
            System.out.println(">>> Email: admin@gmail.com");
            System.out.println(">>> Pass : 123456");
            System.out.println("---------------------------------------------");
        }
    }
}
