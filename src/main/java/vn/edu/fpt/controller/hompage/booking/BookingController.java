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
        Long userId = userDetails.getUserId();
        long ticketBought = this.ticketService.countCompletedTicketsByUserAndEvent(userId, eventId);
        long remaining = 3 - ticketBought;
        model.addAttribute("event", eventService.getEventById(eventId));

        if(remaining <= 0) {
            model.addAttribute("limitReached", true);
            model.addAttribute("message", "Bạn đã mua tối đa 3 vé cho sự kiện này.");
            return "homepage/ChooseSeat";
        }
        model.addAttribute("remainingSlots", remaining);
        model.addAttribute("limitReached", false);
        return "homepage/ChooseSeat";
    }
}
