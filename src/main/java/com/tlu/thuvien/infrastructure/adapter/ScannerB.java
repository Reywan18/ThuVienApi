package com.tlu.thuvien.infrastructure.adapter;

import com.tlu.thuvien.infrastructure.utils.QRReaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("scannerB")
public class ScannerB implements QRScanner {
    @Override
    public String scan(MultipartFile imageFile) {
        String coreContent = QRReaderUtils.readQRRaw(imageFile); // VD: "101"

        return "ID#" + coreContent;    }
}