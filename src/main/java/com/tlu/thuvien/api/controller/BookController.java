package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.request.books.BookRequest;
import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.api.dto.response.book.BookResponse;
import com.tlu.thuvien.application.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sách thành công",
                bookService.getAllBooks(keyword)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy chi tiết sách thành công",
                bookService.getBookById(id)
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @ModelAttribute BookRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Thêm sách mới thành công",
                bookService.createBook(request)
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cập nhật sách thành công",
                bookService.updateBook(id, request)
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Xóa sách thành công",
                null
        ));
    }
}