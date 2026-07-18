package vn.edu.fpt.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.repository.SumRevenueByMonthProjection;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.TicketService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.security.CustomOAuth2User;
import vn.edu.fpt.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    public AdminController(UserService userService,
                           EventService eventService,
                           TicketService ticketService) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            @AuthenticationPrincipal CustomOAuth2User oAuth2Users){

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        List<EventSummaryDto> events = eventService.findTop10Events();
        model.addAttribute("events", events);

        long allEvent = eventService.countAllEvent();
        model.addAttribute("allEvent", allEvent);

        long allUserActive = eventService.countAllUseActive();
        model.addAttribute("allUserActive", allUserActive);

        Long allSoldTicket = ticketService.countAllSoldTicket();
        model.addAttribute("allSoldTicket", allSoldTicket);

        List<CountEventByMonthDTO> allEventByMonth = eventService.countEventByMonth();
        model.addAttribute("allEventByMonth", allEventByMonth);

        List<SumRevenueByMonthProjection> sumRevenueByMonth = eventService.sumRevenueByMonth();
        model.addAttribute("sumRevenueByMonth",sumRevenueByMonth);

        List<EventSummaryDto> top5Events = eventService.findTop5EventsBySoldCount();
        model.addAttribute("top5Events", top5Events);

        return "admin/DashboardAdmin";

    }


}
