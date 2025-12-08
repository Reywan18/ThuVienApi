package com.tlu.thuvien.api.dto.response.cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String author;
}
