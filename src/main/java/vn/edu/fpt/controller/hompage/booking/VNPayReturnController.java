package vn.edu.fpt.controller.hompage.booking;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.modelview.response.homepage.VNPayReturnResult;
import vn.edu.fpt.service.impl.VNPayService;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class VNPayReturnController {

    private final VNPayService vnPayService;

    @GetMapping("/vnpay/return")
    public String vnpayReturn(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Map<String, String> params = extractParams(request);

        VNPayReturnResult result = vnPayService.processReturn(params);

        redirectAttributes.addFlashAttribute("paymentStatus", result.getStatus());
        redirectAttributes.addFlashAttribute("paymentMessage", result.getMessage());
        redirectAttributes.addFlashAttribute("paymentOrderId", result.getOrderId());

        if (result.getEventId() != null) {
            return "redirect:/events/detail/" + result.getEventId();
        }
        return "redirect:/events";
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }
}