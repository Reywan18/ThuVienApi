package com.tlu.thuvien.infrastructure.adapter;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FileImageScannerAdapter implements QRScanner {
    private final MultipartFile file;
    public FileImageScannerAdapter(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String scan() {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new RuntimeException("File tải lên không phải là ảnh hợp lệ!");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);

            return result.getText();

        } catch (NotFoundException e) {
            throw new RuntimeException("Không tìm thấy mã QR nào trong ảnh này.");
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file ảnh: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý QR: " + e.getMessage());
        }
    }
}