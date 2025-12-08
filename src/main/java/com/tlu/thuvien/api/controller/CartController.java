package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.api.dto.response.cart.CartResponse;
import com.tlu.thuvien.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy giỏ hàng thành công",
                cartService.getMyCart()
        ));
    }

    @PostMapping("/add/{bookId}")
    public ResponseEntity<ApiResponse<Void>> addToCart(@PathVariable Long bookId) {
        cartService.addToCart(bookId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Thêm sách vào giỏ thành công",
                null
        ));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Xóa sách khỏi giỏ thành công",
                null
        ));
    }
}