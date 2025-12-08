package com.tlu.thuvien.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ScanQrRequest {
    @Schema(example = "USER:1;DATA=thuvien_secure", description = "Chuỗi mã QR quét được từ máy quét")
    private String qrContent;
}