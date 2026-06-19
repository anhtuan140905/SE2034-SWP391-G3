package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.service.CheckoutService;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.TicketService;
import vn.edu.fpt.service.impl.PaymentService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final TicketService ticketService;

    // Gọi từ trang chọn ghế khi user bấm "Tiến hành thanh toán"
    @PostMapping("/proceed")
    public String proceedToPayment(@RequestParam("seatIds") List<Long> seatIds,
                                   @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = currentUser.getUser();
        try {
            Long orderId = checkoutService.proceedToPayment(seatIds, user);
            return "redirect:/checkout/" + orderId;
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return "redirect:/events/1/choose_seat?error=conflict";
        }
    }
    @PostMapping("/toggle-lock")
    public ResponseEntity<?> toggleSeatLock(@RequestParam("seatId") Long seatId,
                                            @RequestParam("action") String action, // "LOCK" hoặc "UNLOCK"
                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
        }

        try {
            if ("LOCK".equals(action)) {
                checkoutService.lockSeatTemporarily(seatId, currentUser.getUser());
            } else {
                checkoutService.unlockSeatTemporarily(seatId, currentUser.getUser());
            }
            return ResponseEntity.ok().body(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/success/{orderId}")
    @ResponseBody // <── CHÍ MẠNG: Giúp chạy cơ chế API ngay trong @Controller thường
    @Transactional
    public ResponseEntity<?> autoConfirmPayment(@PathVariable("orderId") Long orderId) {
        try {
            // 1. Lấy thông tin Order ra kiểm tra
            Order order = this.orderService.findById(orderId);

            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                return ResponseEntity.badRequest().body("Đơn hàng này đã được xử lý trước đó rồi.");
            }

            // 2. Tìm Payment đi kèm
            Payment payment = this.paymentService.findById(orderId);
            if(payment == null){
                throw new IllegalArgumentException("Không tìm thấy thông tin thanh toán");
            }

            // 3. Tự động cập nhật trạng thái thành công dưới DB
            order.setStatus(OrderStatus.PAID);
            payment.setStatus(PaymentStatus.SUCCESS);
            this.orderService.handleSaveOrder(order);
            this.paymentService.handleSavePayment(payment);
            this.ticketService.handleSaveTicketByOrder(order);
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi xử lý hệ thống: " + e.getMessage());
        }
    }


}