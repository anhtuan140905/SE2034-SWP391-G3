package vn.edu.fpt.controller.organizer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.TicketService;

@Controller
@RequestMapping("/organizer/")
@AllArgsConstructor
public class CheckInController {
    private TicketService ticketService;
    @GetMapping("event/{id}/checkIn")
    public String checkin(@PathVariable("id")Long eventId, Model model){
        Integer totalTicketsSold = ticketService.countAllticketSelledOfEvent(eventId);
        Long checked = ticketService.countTicketCheckInByEvent(eventId);
        double percent = totalTicketsSold > 0 ? (checked * 100.0 / totalTicketsSold) : 0;
        double offset = 238.76 * (1 - percent / 100);
        model.addAttribute("eventId", eventId);
        model.addAttribute("percent", Math.round(percent));
        model.addAttribute("checkinDetails",ticketService.getDetailCheckinByTicketType(eventId));
        model.addAttribute("offset", offset);
        model.addAttribute("totalTicketsSold",totalTicketsSold);
        model.addAttribute("checked",checked);
       return "organizer/staff/CheckIn";
    }
}
