package com.tlu.thuvien.api.controller.mvc;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.tlu.thuvien.application.service.BorrowService;
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import com.tlu.thuvien.infrastructure.adapter.FileImageScannerAdapter;
import com.tlu.thuvien.infrastructure.adapter.QRAdapter;
import com.tlu.thuvien.infrastructure.adapter.QRScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/borrows")
@RequiredArgsConstructor
public class AdminBorrowController {

    private final BorrowService borrowService;
    private final QRAdapter qrAdapter;

    // --- TRANG 1: DUYỆT MƯỢN SÁCH (PENDING) ---
    @GetMapping("/pending")
    public String showApprovePage(Model model) {
        model.addAttribute("transactions", borrowService.getPendingTransactions());
        return "admin/borrow-approve";
    }

    // --- TRANG 2: TRẢ SÁCH (BORROWED) ---
    @GetMapping("/active")
    public String showReturnPage(Model model) {
        model.addAttribute("transactions", borrowService.getBorrowedTransactions());
        return "admin/borrow-return";
    }

    // --- XỬ LÝ: DUYỆT BẰNG QR ---
    @PostMapping("/approve-qr")
    public String approveByQR(@RequestParam("qrImage") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ảnh QR!");
            return "redirect:/admin/borrows/pending";
        }

        try {
            QRScanner scanner = new FileImageScannerAdapter(file);
            String rawQrData = scanner.scan();

            Long userId = qrAdapter.parseUserId(rawQrData);

            BorrowTransaction transaction = borrowService.getPendingTransactionByUserId(userId);

            if (transaction != null) {
                borrowService.approveBorrow(transaction.getId());
                redirectAttributes.addFlashAttribute("successMessage",
                        "Quét thành công! Đã duyệt phiếu #" + transaction.getId() + " cho User: " + transaction.getUser().getEmail());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không tìm thấy yêu cầu mượn mới nào cho User ID: " + userId);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý QR: " + e.getMessage());
        }

        return "redirect:/admin/borrows/pending"; // Quay lại trang duyệt
    }

    @PostMapping("/reject/{id}")
    public String rejectBorrow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi service để hủy yêu cầu
            borrowService.rejectBorrow(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa yêu cầu mượn #" + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/borrows/pending";
    }

    // --- XỬ LÝ: TRẢ SÁCH ---
    @PostMapping("/return/{id}")
    public String returnBorrow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String resultMessage = borrowService.processReturn(id);
            redirectAttributes.addFlashAttribute("successMessage", resultMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/borrows/active"; // Quay lại trang trả sách
    }
}