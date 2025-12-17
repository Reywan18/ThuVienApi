package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.request.ScanQrRequest;
import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.application.service.BorrowService;
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import com.tlu.thuvien.infrastructure.adapter.QRAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final QRAdapter qrAdapter;
    private final BorrowService borrowService;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN')")
    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> scanQrCode(
            @RequestBody ScanQrRequest request,
            @RequestParam(defaultValue = "borrow") String type
    ) {
        String rawQrData = request.getQrContent();
        Long userId = qrAdapter.parseUserId(rawQrData);

        BorrowTransaction transaction;
        String message;

        if ("return".equalsIgnoreCase(type)) {
            transaction = borrowService.getBorrowedTransactionByUserId(userId);
            message = "Quét thành công! Tìm thấy sách đang mượn.";
        } else {
            transaction = borrowService.getPendingTransactionByUserId(userId);
            message = "Quét thành công! Tìm thấy yêu cầu mượn mới.";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("action", type.toUpperCase());
        result.put("userName", transaction.getUser().getName());
        result.put("userEmail", transaction.getUser().getEmail());
        result.put("transactionId", transaction.getId());

        List<String> books = transaction.getDetails().stream()
                .map(detail -> detail.getBook().getTitle())
                .collect(Collectors.toList());
        result.put("books", books);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                message,
                result
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN')")
    @PostMapping("/approve/{transactionId}")
    public ResponseEntity<ApiResponse<Void>> approveBorrow(@PathVariable Long transactionId) {
        borrowService.approveBorrow(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Đã duyệt phiếu mượn thành công!",
                null
        ));
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @PostMapping("/return/{transactionId}")
    public ResponseEntity<ApiResponse<String>> processReturn(@PathVariable Long transactionId) {
        String message = borrowService.processReturn(transactionId);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                message,
                null
        ));
    }
}