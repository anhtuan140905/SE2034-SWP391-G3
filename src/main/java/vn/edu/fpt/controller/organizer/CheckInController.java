package vn.edu.fpt.controller.organizer;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.TicketService;

@Controller
@RequestMapping("/organizer/")
@AllArgsConstructor
public class CheckInController {
    private TicketService ticketService;
    private StaffService staffService;
    @GetMapping("event/{id}/checkIn")
    public String checkin(@PathVariable("id")Long eventId, Model model, @AuthenticationPrincipal AuthenticatedUser userDetails){
        if(!staffService.checkPermission(userDetails.getUser().getId(),eventId,"STAFF_CHECKIN_LIST_VIEW")){
            model.addAttribute("eventId", eventId);
            return "organizer/Forbidden";
        }
        Integer totalTicketsSold = ticketService.countAllticketSelledOfEvent(eventId);
        Long checked = ticketService.countTicketCheckInByEvent(eventId);
//        fix
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
