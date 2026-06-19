package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Payment;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.OrderService;

@Controller
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
public class BookingController {

    private final EventService eventService;

    // Bước 1: Chọn ghế trên sơ đồ
    @GetMapping("/choose_seat")
    public String chooseSeat(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        return "homepage/ChooseSeat";
    }
}
