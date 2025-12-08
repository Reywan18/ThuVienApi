package com.tlu.thuvien.infrastructure.adapter;

import org.springframework.stereotype.Component;

@Component("scannerA")
public class ScannerA implements QRScanner {
    @Override
    public String scan() {
        return "USER:101;DATA=encrypted_info";
    }
}
