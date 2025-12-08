package com.tlu.thuvien.api.dto.response.book;

import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
}