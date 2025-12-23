package com.tlu.thuvien.application.service;

import com.tlu.thuvien.api.dto.request.books.BookRequest;
import com.tlu.thuvien.api.dto.response.book.BookResponse;
import com.tlu.thuvien.domain.builder.BookBuilder;
import com.tlu.thuvien.domain.entity.Book;
import com.tlu.thuvien.infrastructure.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAllBooks(String keyword) {
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.searchBooks(keyword);
        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
        return mapToResponse(book);
    }

    public BookResponse createBook(BookRequest request) {
        try {
            String base64Image = null;
            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                base64Image = Base64.getEncoder().encodeToString(request.getImageFile().getBytes());
            }

            // Dùng Builder tạo mới
            Book book = new BookBuilder()
                    .title(request.getTitle())
                    .author(request.getAuthor())
                    .category(request.getCategory())
                    .totalQuantity(request.getTotalQuantity())
                    .availableQuantity(request.getTotalQuantity())
                    .image(base64Image)
                    .build();

            return mapToResponse(bookRepository.save(book));

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu sách: " + e.getMessage());
        }
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        try {
            Book oldBook = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));

            String base64Image = oldBook.getImage();
            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                base64Image = Base64.getEncoder().encodeToString(request.getImageFile().getBytes());
            }

            int diff = request.getTotalQuantity() - oldBook.getTotalQuantity();
            int newAvailable = oldBook.getAvailableQuantity() + diff;
            if (newAvailable < 0) newAvailable = 0;

            Book bookToUpdate = new BookBuilder()
                    .id(oldBook.getId())
                    .title(request.getTitle())
                    .author(request.getAuthor())
                    .category(request.getCategory())
                    .totalQuantity(request.getTotalQuantity())
                    .availableQuantity(newAvailable)
                    .image(base64Image)
                    .build();

            return mapToResponse(bookRepository.save(bookToUpdate));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sửa sách: " + e.getMessage());
        }
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sách để xóa");
        }
        bookRepository.deleteById(id);
    }

    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getCategory())
                .totalQuantity(book.getTotalQuantity())
                .availableQuantity(book.getAvailableQuantity())
                .image(book.getImage())
                .build();
    }
}