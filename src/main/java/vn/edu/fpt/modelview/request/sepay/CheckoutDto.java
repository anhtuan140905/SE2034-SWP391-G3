package vn.edu.fpt.modelview.request.sepay;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CheckoutDto {

    @Getter @Setter @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProceedRequest {
        private Long eventId;
        private List<Long> seatIds;   // danh sách ghế đã lock
    }

    // ── Response: trả về trang QR ──────────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QrResponse {
        private Long orderId;
        private String paymentCode;      // EVH{orderId}
        private BigDecimal amount;
        private String vietQrUrl;        // URL ảnh QR img.vietqr.io
        private Instant expiresAt;       // hạn thanh toán để đếm ngược
        private String bankAccountName;
        private String bankAccountNo;
        private String bankName;
    }

    // ── Response: sau khi confirm thành công ──────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConfirmResponse {
        private Long orderId;
        private String message;
        private List<String> ticketCodes; // danh sách mã vé đã tạo
    }
}