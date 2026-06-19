package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.impl.PaymentService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutPageController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    public String showCheckoutPage(@PathVariable Long orderId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
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
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = new User();
        if(userDetails != null) {
            user = userDetails.getUser();
        }
        paymentService.confirmPayment(orderId, user);
        return "redirect:/orders/" + orderId + "/success";
    }
}