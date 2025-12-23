package com.tlu.thuvien.api.dto.response.book;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String image;
}