package vn.edu.fpt.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.configuration.VNPayConfig;
import vn.edu.fpt.model.Payment;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.modelview.response.homepage.VNPayReturnResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final PaymentService paymentService;
    @Autowired
    private VNPayConfig vnPayConfig;

    public String buildPaymentUrl(String txnRef, long amountVND, String orderInfo, String ipAddress, Instant orderExpiresAt) {
        System.out.println("TmnCode loaded: [" + vnPayConfig.getTmnCode() + "]");
        System.out.println("HashSecret loaded: [" + vnPayConfig.getHashSecret() + "]");
        System.out.println("HashSecret length: " + vnPayConfig.getHashSecret().length());
        Map<String, String> params = new TreeMap<>(); // TreeMap tự sort key theo alphabet

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amountVND * 100)); // VNPay yêu cầu x100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(new Date());
        params.put("vnp_CreateDate", createDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(Date.from(orderExpiresAt));
        params.put("vnp_ExpireDate", expireDate);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        System.out.println("=== VNPAY DEBUG ===");
        System.out.println("HashData: " + hashData);
        System.out.println("SecureHash: " + secureHash);
        System.out.println("Full URL: " + vnPayConfig.getPayUrl() + "?" + query);

        return vnPayConfig.getPayUrl() + "?" + query;
    }

    public boolean verifyReturn(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        if (vnpSecureHash == null) return false;

        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = filtered.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return calculatedHash.equals(vnpSecureHash);
    }

    public boolean isPaymentSuccess(Map<String, String> params) {
        return "00".equals(params.get("vnp_ResponseCode"));
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC SHA512", e);
        }
    }

    public VNPayReturnResult processReturn(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");

        // 1. Verify chữ ký
        if (!verifyReturn(params)) {
            return VNPayReturnResult.error("Chữ ký không hợp lệ. Giao dịch có thể đã bị giả mạo.");
        }

        // 2. Tìm Payment
        Optional<Payment> paymentOpt = this.paymentService.findByVnpTxnRef(txnRef);
        if (paymentOpt.isEmpty()) {
            return VNPayReturnResult.error("Không tìm thấy đơn hàng tương ứng.");
        }

        Payment payment = paymentOpt.get();

        // 3. Double-submit guard
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return VNPayReturnResult.success(payment.getOrder().getOrderId(), payment.getOrder().getEvent().getEventId(),
                    "Đơn hàng đã được thanh toán trước đó.");
        }

        // 4. Xử lý kết quả
        if (isPaymentSuccess(params)) {
            payment.setVnpTransactionNo(params.get("vnp_TransactionNo"));
            this.paymentService.save(payment);

            boolean confirmed = paymentService.confirmPaymentByGateway(payment.getOrder().getOrderId());
            if (!confirmed) {
                return VNPayReturnResult.failed(
                        payment.getOrder().getOrderId(),
                        payment.getOrder().getEvent().getEventId(),
                        "Đơn hàng đã hết thời gian thanh toán. Ghế đã được mở lại."
                );
            }
            return VNPayReturnResult.success(payment.getOrder().getOrderId(), payment.getOrder().getEvent().getEventId(), "Thanh toán thành công!");
        } else {
            paymentService.failPaymentByGateway(payment.getOrder().getOrderId());

            return VNPayReturnResult.failed(payment.getOrder().getOrderId(), payment.getOrder().getEvent().getEventId(),
                    "Thanh toán thất bại hoặc đã bị hủy. Mã lỗi: " + params.get("vnp_ResponseCode"));
        }
    }

    /**
     * Lấy IP thật của client, có xét trường hợp qua proxy/load balancer.
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}