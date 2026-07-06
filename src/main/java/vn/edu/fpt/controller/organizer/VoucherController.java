package vn.edu.fpt.controller.organizer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.Voucher;
import vn.edu.fpt.model.constant.DiscountType;
import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;
import vn.edu.fpt.service.VoucherService;
import vn.edu.fpt.security.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("organizer/event/{eventId}/voucher")
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public String listVoucher(@PathVariable Long eventId, Model model) {
        List<Voucher> vouchers = voucherService.getVoucherByEventId(eventId);

        model.addAttribute("eventId", eventId);
        model.addAttribute("vouchers", vouchers);

        return "organizer/voucher/VoucherManagement";
    }

    @GetMapping("/{voucherId}")
    public String voucherDetail(@PathVariable Long eventId,
                                @PathVariable Long voucherId,
                                Model model) {
        Voucher voucher = voucherService.getVoucherDetail(eventId, voucherId);

        model.addAttribute("eventId", eventId);
        model.addAttribute("voucher", voucher);

        return "organizer/voucher/ViewVoucher";
    }

    @GetMapping("create")
    public String createVoucherForm(@PathVariable Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("discountType", DiscountType.values());

        if (!model.containsAttribute("createVoucherRequest")) {
            model.addAttribute("createVoucherRequest", new CreateVoucherRequest());
        }

        return "organizer/voucher/CreateVoucher";
    }

    @PostMapping("/create")
    public String createVoucher(@PathVariable Long eventId,
                                @Valid @ModelAttribute("createVoucherRequest") CreateVoucherRequest request,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventId", eventId);
            model.addAttribute("discountType", DiscountType.values());
            System.out.println("VALIDATION ERRORS: " + bindingResult.getAllErrors());

            return "organizer/voucher/CreateVoucher";
        }

        try {
            voucherService.createVoucher(eventId, customUserDetails.getUserId(), request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("eventId", eventId);
            model.addAttribute("discountType", DiscountType.values());
            model.addAttribute("error", e.getMessage());
            System.out.println("BUSINESS ERROR: " + e.getMessage());

            return "organizer/voucher/CreateVoucher";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Tạo voucher thành công!");
        return "redirect:/organizer/event/" + eventId + "/voucher";
    }

}









