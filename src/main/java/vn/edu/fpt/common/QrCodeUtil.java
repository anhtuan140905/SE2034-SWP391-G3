package vn.edu.fpt.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QrCodeUtil {

        public static byte[] generateQrPng(String content, int size) {
            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(matrix, "PNG", out);
                return out.toByteArray();
            } catch (WriterException | IOException e) {
                throw new RuntimeException("Không tạo được mã QR", e);
            }

    }
}
