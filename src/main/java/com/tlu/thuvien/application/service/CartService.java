package com.tlu.thuvien.application.service;

import com.tlu.thuvien.api.dto.response.cart.CartResponse;
import com.tlu.thuvien.api.dto.response.cart.CartItemResponse;
import com.tlu.thuvien.domain.entity.*;
import com.tlu.thuvien.infrastructure.repository.BookRepository;
import com.tlu.thuvien.infrastructure.repository.CartRepository;
import com.tlu.thuvien.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu: User chưa có giỏ hàng"));

        return CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems().stream().map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .bookId(item.getBook().getId())
                        .bookTitle(item.getBook().getTitle())
                        .author(item.getBook().getAuthor())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void addToCart(Long bookId) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Sách này đã hết, vui lòng chọn sách khác");
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .book(book)
                .build();

        cart.getItems().add(item);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
    }
}