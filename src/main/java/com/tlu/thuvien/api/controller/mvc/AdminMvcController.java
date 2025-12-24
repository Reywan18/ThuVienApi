package com.tlu.thuvien.api.controller.mvc;

import com.tlu.thuvien.api.dto.request.books.BookRequest;
import com.tlu.thuvien.api.dto.request.user.UserRequest;
import com.tlu.thuvien.application.service.BookService;
import com.tlu.thuvien.application.service.UserService;
import com.tlu.thuvien.domain.entity.Book;
import com.tlu.thuvien.domain.entity.User;
import com.tlu.thuvien.domain.entity.UserRole;
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

    // Trang Quản lý User
    @GetMapping("/users")
    public String userList(Model model,
                           @RequestParam(name = "keyword", required = false) String keyword,
                           @RequestParam(name = "role", required = false) UserRole role) {
        model.addAttribute("users", userService.getAllUsers(keyword, role));

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);

        return "admin/users/list";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "admin/users/form";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);

        UserRequest request = new UserRequest();
        request.setId(user.getId());
        request.setName(user.getName());
        request.setEmail(user.getEmail());
        request.setRole(user.getRole());

        model.addAttribute("user", request);
        return "admin/users/form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserRequest userRequest,
                           RedirectAttributes redirectAttributes) {
        try {
            if (userRequest.getId() != null) {
                userService.updateUser(userRequest.getId(), userRequest);
                redirectAttributes.addFlashAttribute("success", "Cập nhật User thành công!");
            } else {
                userService.createUser(userRequest);
                redirectAttributes.addFlashAttribute("success", "Thêm User mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/users/create";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xóa User thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa (User đang mượn sách hoặc có lỗi).");
        }
        return "redirect:/admin/users";
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
