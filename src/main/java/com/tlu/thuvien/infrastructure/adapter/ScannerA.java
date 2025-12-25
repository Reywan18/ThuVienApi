package com.tlu.thuvien.infrastructure.adapter;

import com.tlu.thuvien.infrastructure.utils.QRReaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("scannerA")
public class ScannerA implements QRScanner {
    @Override
    public String scan(MultipartFile imageFile) {
        String coreContent = QRReaderUtils.readQRRaw(imageFile); // VD: "101"

        return "USER:" + coreContent + ";SECURE_V1";
    }
}
