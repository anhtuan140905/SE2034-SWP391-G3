package vn.edu.fpt.controller.moderator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.ModeratorDashboardService;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorDashboardController {

    private final ModeratorDashboardService moderatorDashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", moderatorDashboardService.getDashboardStats());
        model.addAttribute("recentEvents", moderatorDashboardService.getRecentEvents());
        model.addAttribute("todayEvents", moderatorDashboardService.getTodayEvents());
        model.addAttribute("activePage", "dashboard");

        return "moderator/DashboardModerator";
    }

}