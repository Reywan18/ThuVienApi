package com.tlu.thuvien.api.controller;

import com.tlu.thuvien.api.dto.response.api.ApiResponse;
import com.tlu.thuvien.application.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {
    private final BorrowService borrowService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createBorrowRequest() {
        Long transactionId = borrowService.createBorrowRequest();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Yêu cầu mượn thành công! Vui lòng đưa mã QR cho thủ thư duyệt.",
                transactionId
        ));
    }
}