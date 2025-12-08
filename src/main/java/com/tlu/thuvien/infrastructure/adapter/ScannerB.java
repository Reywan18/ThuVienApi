package com.tlu.thuvien.infrastructure.adapter;

import org.springframework.stereotype.Component;

@Component("scannerB")
public class ScannerB implements QRScanner {
    @Override
    public String scan() {
        return "ID#102";
    }
}