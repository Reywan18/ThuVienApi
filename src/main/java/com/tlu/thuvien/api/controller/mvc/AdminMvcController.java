package com.tlu.thuvien.api.controller.mvc;

import com.tlu.thuvien.api.dto.request.books.BookRequest;
import com.tlu.thuvien.application.service.BookService;
import com.tlu.thuvien.application.service.UserService;
import com.tlu.thuvien.domain.entity.Book;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMvcController {
    private final UserService userService;
    private final BookService bookService;

    //Trang Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "admin/dashboard"; // Trỏ đến file templates/admin/dashboard.html
    }

    // Trang Quản lý User
    @GetMapping("/users")
    public String userList(Model model) {
        // Lấy dữ liệu từ Service và đẩy sang View
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    // Trang Quản lý Sách
    @GetMapping("/books")
    public String bookList(Model model,
                           @RequestParam(name = "keyword", required = false) String keyword) {
        model.addAttribute("books", bookService.getAllBooks(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/books/list";
    }

    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/books/form";
    }

    // Form Sửa Sách
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "admin/books/form";
    }

    // Xử lý Lưu Sách (Dùng chung cho Tạo mới và Sửa)
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute BookRequest bookRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/books/form";
        }

        try {
            if (bookRequest.getId() != null) {
                bookService.updateBook(bookRequest.getId(), bookRequest);
                redirectAttributes.addFlashAttribute("success", "Cập nhật sách thành công!");
            } else {
                bookService.createBook(bookRequest);
                redirectAttributes.addFlashAttribute("success", "Thêm sách mới thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
}
