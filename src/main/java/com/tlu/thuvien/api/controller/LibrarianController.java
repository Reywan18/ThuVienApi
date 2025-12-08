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
    public ResponseEntity<ApiResponse<Map<String, Object>>> scanQrCode(@RequestBody ScanQrRequest request) {
        String rawQrData = request.getQrContent();

        Long userId = qrAdapter.parseUserId(rawQrData);

        BorrowTransaction transaction = borrowService.getPendingTransactionByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userName", transaction.getUser().getName());
        result.put("userEmail", transaction.getUser().getEmail());
        result.put("transactionId", transaction.getId());

        List<String> books = transaction.getDetails().stream()
                .map(detail -> detail.getBook().getTitle())
                .collect(Collectors.toList());
        result.put("books", books);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Quét thành công! Tìm thấy yêu cầu mượn.",
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
}