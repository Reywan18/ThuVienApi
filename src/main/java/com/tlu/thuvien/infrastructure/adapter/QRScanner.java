package com.tlu.thuvien.infrastructure.adapter;

import org.springframework.web.multipart.MultipartFile;

public interface QRScanner {
    String scan(MultipartFile imageFile);
}
