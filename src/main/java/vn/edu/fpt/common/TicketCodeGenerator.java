package vn.edu.fpt.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TicketCodeGenerator {

    // Mã hiển thị cho user: ngắn, dễ đọc, vẫn đủ random để không trùng
    public String generateTicketCode(Long orderId, Long seatId) {
        String shortRandom = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TKT-" + orderId + "-" + seatId + "-" + shortRandom;
    }

    // Nội dung QR — UUID đầy đủ, không liên quan ticketCode, dùng để scan check-in
    public String generateQrCode() {
        return UUID.randomUUID().toString();
    }
}