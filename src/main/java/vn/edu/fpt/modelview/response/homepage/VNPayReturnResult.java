package vn.edu.fpt.modelview.response.homepage;

import lombok.Getter;

@Getter
public class VNPayReturnResult {
    private final String status; // "success" | "failed" | "error"
    private final String message;
    private final Long orderId;
    private final Long eventId;


    private VNPayReturnResult(String status, String message, Long orderId, Long eventId) {
        this.status = status;
        this.message = message;
        this.orderId = orderId;
        this.eventId = eventId;
    }

    public static VNPayReturnResult success(Long orderId, Long eventId, String message) {
        return new VNPayReturnResult("success", message, orderId, eventId);
    }

    public static VNPayReturnResult failed(Long orderId, Long eventId, String message) {
        return new VNPayReturnResult("failed", message, orderId, eventId);
    }

    public static VNPayReturnResult error(String message) {
        return new VNPayReturnResult("error", message, null, null);
    }
}