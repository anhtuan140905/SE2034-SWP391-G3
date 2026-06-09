package vn.edu.fpt.controller.moderator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.impl.EventServiceImpl;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorDashboardController {

    private final EventServiceImpl eventService;
    private EventRepository eventRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("stats", eventService.getDashboardStats());
        model.addAttribute("pendingEvents", eventService.getTopThreePendingEvents());
        model.addAttribute("todayEvents", eventService.getTodayActiveEvents());
        model.addAttribute("activePage", "dashboard");

        return "moderator/DashboardModerator";

    }

}
