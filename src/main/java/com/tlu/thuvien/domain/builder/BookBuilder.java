package com.tlu.thuvien.domain.builder;

import com.tlu.thuvien.domain.entity.Book;

public class BookBuilder {
    private Long id;
    private String title;
    private String author;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String image;

    public BookBuilder() {
    }

    public BookBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public BookBuilder title(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder author(String author) {
        this.author = author;
        return this;
    }

    public BookBuilder category(String category) {
        this.category = category;
        return this;
    }

    public BookBuilder totalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
        return this;
    }

    public BookBuilder availableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public BookBuilder image(String image) {
        this.image = image;
        return this;
    }

    public Book build() {
        return new Book(id, title, author, category, totalQuantity, availableQuantity, image);
    }
}
