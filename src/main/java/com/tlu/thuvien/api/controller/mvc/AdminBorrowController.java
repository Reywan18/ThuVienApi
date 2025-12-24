package com.tlu.thuvien.api.controller.mvc;

import com.tlu.thuvien.application.service.BorrowService;
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/borrows")
@RequiredArgsConstructor
public class AdminBorrowController {
    private final BorrowService borrowService;

    // 1. Hiển thị danh sách phiếu mượn
    @GetMapping
    public String listBorrows(Model model) {
        List<BorrowTransaction> transactions = borrowService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "admin/borrow-list"; // Trả về file template borrow-list.html
    }

    // 2. Duyệt phiếu mượn (Approve)
    @PostMapping("/approve/{id}")
    public String approveBorrow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowService.approveBorrow(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt phiếu mượn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    // 3. Xác nhận trả sách (Return)
    @PostMapping("/return/{id}")
    public String returnBorrow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String resultMessage = borrowService.processReturn(id);
            redirectAttributes.addFlashAttribute("successMessage", resultMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }
}
