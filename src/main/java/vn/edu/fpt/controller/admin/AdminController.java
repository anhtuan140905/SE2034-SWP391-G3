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
import vn.edu.fpt.service.impl.EventServiceImpl;
import vn.edu.fpt.service.impl.TicketServiceImpl;
import vn.edu.fpt.service.impl.UserServiceImpl;
import vn.edu.fpt.security.CustomOAuth2User;
import vn.edu.fpt.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImpl;
    private final EventServiceImpl eventServiceImpl;
    private final TicketServiceImpl ticketServiceImpl;

    public AdminController(UserServiceImpl userServiceImpl,
                           EventServiceImpl eventServiceImpl,
                           TicketServiceImpl ticketServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.eventServiceImpl = eventServiceImpl;
        this.ticketServiceImpl = ticketServiceImpl;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            @AuthenticationPrincipal CustomOAuth2User oAuth2Users){

        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        List<EventSummaryDto> events = eventServiceImpl.findTop10Events();
        model.addAttribute("events", events);

        long allEvent = eventServiceImpl.countAllEvent();
        model.addAttribute("allEvent", allEvent);

        long allUserActive = eventServiceImpl.countAllUseActive();
        model.addAttribute("allUserActive", allUserActive);

        long allSoldTicket = ticketServiceImpl.countAllSoldTicket();
        model.addAttribute("allSoldTicket", allSoldTicket);

        List<CountEventByMonthDTO> allEventByMonth = eventServiceImpl.countEventByMonth();
        model.addAttribute("allEventByMonth", allEventByMonth);

        List<SumRevenueByMonthProjection> sumRevenueByMonth = eventServiceImpl.sumRevenueByMonth();
        model.addAttribute("sumRevenueByMonth",sumRevenueByMonth);

        List<EventSummaryDto> top5Events = eventServiceImpl.findTop5EventsBySoldCount();
        model.addAttribute("top5Events", top5Events);

        return "admin/DashboardAdmin";

    }


}
