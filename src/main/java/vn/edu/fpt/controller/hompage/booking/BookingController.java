package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Payment;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.TicketService;

@Controller
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
public class BookingController {

    private final EventService eventService;
    private final TicketService ticketService;

    // Bước 1: Chọn ghế trên sơ đồ
    @GetMapping("/choose_seat")
    public String chooseSeat(
            @PathVariable Long eventId,
            Model model,
            @AuthenticationPrincipal AuthenticatedUser userDetails) {
        model.addAttribute("event", eventService.getEventById(eventId));

        if (userDetails == null) {
            model.addAttribute("limitReached", false);
            model.addAttribute("remainingSlots", 3);
            model.addAttribute("isAuthenticated", false);
            return "homepage/ChooseSeat";
        }
        model.addAttribute("isAuthenticated", true);
        Long userId = userDetails.getUserId();
        int remaining = ticketService.getRemainingTicketQuota(userId, eventId);
        if (remaining <= 0) {
            model.addAttribute("limitReached", true);
            model.addAttribute("message", "Bạn đã mua tối đa số lượng vé cho phép cho sự kiện này.");
            return "homepage/ChooseSeat";
        }
        model.addAttribute("remainingSlots", 3 - remaining);
        model.addAttribute("limitReached", false);
        return "homepage/ChooseSeat";
    }
}
