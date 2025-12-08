package com.tlu.thuvien.application.service;

import com.tlu.thuvien.api.dto.request.book.BookRequest;
import com.tlu.thuvien.api.dto.response.book.BookResponse;
import com.tlu.thuvien.application.mapper.BookMapper;
import com.tlu.thuvien.domain.builder.BookBuilder;
import com.tlu.thuvien.domain.entity.Book;
import com.tlu.thuvien.infrastructure.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookResponse> getAllBooks(String keyword) {
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.searchBooks(keyword);
        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(bookMapper::toResponse).collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
        return bookMapper.toResponse(book);
    }

    public BookResponse createBook(BookRequest request) {
        Book book = new BookBuilder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .category(request.getCategory())
                .totalQuantity(request.getTotalQuantity())
                .availableQuantity(request.getTotalQuantity())
                .build();

        return bookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());

        int diff = request.getTotalQuantity() - book.getTotalQuantity();
        book.setTotalQuantity(request.getTotalQuantity());
        book.setAvailableQuantity(book.getAvailableQuantity() + diff);

        return bookMapper.toResponse(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sách để xóa");
        }
        bookRepository.deleteById(id);
    }
}