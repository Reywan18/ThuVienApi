package com.tlu.thuvien.api.controller.mvc;

import com.tlu.thuvien.application.service.BorrowService;
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import com.tlu.thuvien.infrastructure.adapter.QRAdapter;
import com.tlu.thuvien.infrastructure.adapter.QRScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/borrows")
@RequiredArgsConstructor
public class AdminBorrowController {

    private final BorrowService borrowService;
    private final QRAdapter qrAdapter;
    private final Map<String, QRScanner> scanners;

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
    @PostMapping("/simulate-scan")
    public String simulateScan(@RequestParam("qrImage") MultipartFile file,
                               @RequestParam("deviceType") String deviceType, // Nhận vào: "scannerA" hoặc "scannerB"
                               RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ảnh QR!");
            return "redirect:/admin/borrows/pending";
        }

        try {
            QRScanner selectedScanner = scanners.get(deviceType);

            if (selectedScanner == null) {
                throw new RuntimeException("Thiết bị máy quét không tồn tại: " + deviceType);
            }

            String rawOutput = selectedScanner.scan(file);

            Long userId = qrAdapter.parseUserId(rawOutput);

            BorrowTransaction transaction = borrowService.getPendingTransactionByUserId(userId);

            if (transaction != null) {
                borrowService.approveBorrow(transaction.getId());

                String message = String.format(
                        "<b>[%s]</b> Quét thành công!<br/>" +
                                "- Raw Output: <code>%s</code><br/>" +
                                "- Parsed ID: <b>%d</b><br/>" +
                                "- Đã duyệt phiếu cho: %s",
                        deviceType.toUpperCase(), rawOutput, userId, transaction.getUser().getEmail()
                );
                redirectAttributes.addFlashAttribute("successMessage", message);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "[" + deviceType + "] Đọc được User ID " + userId + " nhưng không có phiếu chờ.");
            }

        } catch (Exception e) {
            // e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý: " + e.getMessage());
        }

        return "redirect:/admin/borrows/pending";
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