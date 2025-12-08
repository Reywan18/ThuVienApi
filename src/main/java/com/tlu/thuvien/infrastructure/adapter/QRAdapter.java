package com.tlu.thuvien.infrastructure.adapter;

import org.springframework.stereotype.Component;

@Component
public class QRAdapter {

    public Long parseUserId(String rawData) {
        try {
            if (rawData.startsWith("USER:")) {
                String[] parts = rawData.split(":");
                String idPart = parts[1].split(";")[0];
                return Long.parseLong(idPart);
            }

            if (rawData.startsWith("ID#")) {
                return Long.parseLong(rawData.substring(3));
            }

            throw new RuntimeException("Định dạng QR không hỗ trợ");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc mã QR: " + rawData);
        }
    }
}
