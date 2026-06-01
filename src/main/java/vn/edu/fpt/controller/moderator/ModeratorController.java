package vn.edu.fpt.controller.moderator;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    @GetMapping("/dashboard")
    public String moderator(Model model) {
        return "moderator/DashboardModerator";
    }

    @GetMapping("/events")
    public String eventManagement() {
        return "moderator/EventManagement";
    }

    @GetMapping("/organizers")
    public String organizerApproval() {
        return  "moderator/OrganizerApproval";
    }

    @GetMapping("/profile")
    public String moderatorProfile() {
        return  "moderator/ModeratorProfile";
    }

}