package vn.edu.fpt.controller.hompage.booking;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.impl.PaymentService;
import vn.edu.fpt.service.impl.VNPayService;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final TicketService ticketService;
    private final SeatLockService seatLockService;
    private final VNPayService vnPayService;

    // Gọi từ trang chọn ghế khi user bấm "Tiến hành thanh toán"
    @PostMapping("/proceed")
    public String proceedToPayment(@RequestParam("seatIds") List<Long> seatIds,
                                   @AuthenticationPrincipal AuthenticatedUser currentUser) {
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
                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
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
    @Transactional
    public String autoConfirmPayment(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }
        Order order = this.orderService.findById(orderId);
        if(order == null || !order.getUser().getEmail().equals(currentUser.getUser().getEmail())) {
            redirectAttributes.addFlashAttribute("toastType", "danger");
            redirectAttributes.addFlashAttribute("toastMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/events";
        }

        if(order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            redirectAttributes.addFlashAttribute("toastType", "danger");
            redirectAttributes.addFlashAttribute("toastMessage", "Đơn hàng này đã được xử lý trước đó rồi.");
            return "redirect:/my-tickets";
        }

        Payment payment = this.paymentService.findByOrderId(orderId);
        if(payment == null) {
            redirectAttributes.addFlashAttribute("toastType", "danger");
            redirectAttributes.addFlashAttribute("toastMessage", "Không tìm thấy thông tin thanh toán");
            return "redirect:/events";
        }

        order.setStatus(OrderStatus.PAID);
        payment.setStatus(PaymentStatus.SUCCESS);
        this.orderService.handleSaveOrder(order);
        this.paymentService.handleSavePayment(payment);
        this.ticketService.handleSaveTicketByOrder(order);
        redirectAttributes.addFlashAttribute("toastType", "success");
        redirectAttributes.addFlashAttribute("toastMessage", "Thanh toán thành công, kiểm tra vé trong phần vé của tôi và email");
        return "redirect:/my-tickets";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Long orderId,
                              @AuthenticationPrincipal AuthenticatedUser currentUser,
                              RedirectAttributes redirectAttributes) {
        if(currentUser == null) {
            return "redirect:/login";
        }
        Order order = this.orderService.findById(orderId);
        Long eventId = order != null ? order.getEvent().getEventId() : null;
        this.orderService.handleUpdateStatusOrder(orderId);

        this.seatLockService.handleDeleteSeatLockByOrder(orderId);
        redirectAttributes.addFlashAttribute("paymentStatus", "failed");
        redirectAttributes.addFlashAttribute("paymentMessage",
                "Chưa thanh toán thành công, ghế sẽ được hủy block");
        if (eventId != null) {
            return "redirect:/events/detail/" + eventId;
        }
        return "redirect:/events";
    }

    @GetMapping("/{orderId}")
    public String showCheckoutPage(@PathVariable Long orderId,
                                   @AuthenticationPrincipal AuthenticatedUser userDetails,
                                   Model model) {
        User user = new User();
        if(userDetails != null) {
            user = userDetails.getUser();
        }
        Order order = orderService.getOrderForCheckout(orderId, user);

        model.addAttribute("order", order);
        model.addAttribute("payment", order.getPayment());
        return "homepage/Checkout"; // templates/checkout/checkout.html
    }

    @PostMapping("/{orderId}/confirm")
    public String confirmPayment(@PathVariable Long orderId,
                                 @AuthenticationPrincipal AuthenticatedUser userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Order order = this.orderService.findById(orderId);
            paymentService.confirmPayment(orderId, userDetails.getUser());
            redirectAttributes.addFlashAttribute("paymentStatus", "success");
            redirectAttributes.addFlashAttribute("paymentMessage",
                    "Thanh toán thành công, kiểm tra vé trong phần vé của tôi và email");
            return "redirect:/events/detail/" + order.getEvent().getEventId();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("toastType", "danger");
            redirectAttributes.addFlashAttribute("toastMessage", ex.getMessage());
            return "redirect:/checkout/" + orderId;
        }
    }

    @PostMapping("/{orderId}/pay-vnpay")
    public String payWithVnpay(@PathVariable Long orderId,
                               @AuthenticationPrincipal AuthenticatedUser currentUser,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.prepareVnpayPayment(orderId, currentUser.getUser());

            String ipAddress = VNPayService.getClientIpAddress(request);

            String paymentUrl = vnPayService.buildPaymentUrl(
                    payment.getVnpTxnRef(),
                    payment.getAmount().longValue(),
                    "Thanh toan don hang " + orderId,
                    ipAddress
            );

            return "redirect:" + paymentUrl;

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/checkout/" + orderId;
        }
    }
}