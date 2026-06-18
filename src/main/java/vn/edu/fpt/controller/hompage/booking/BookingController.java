package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.EventService;

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

    // Bước 2: Chọn vé theo loại (tab kia)
    @GetMapping("/tickets")
    public String chooseTicket(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        return "homepage/ChooseSeat";
    }

    // Bước 3: Trang checkout
    @GetMapping("/checkout")
    public String checkout(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        return "homepage/checkout";
    }
}