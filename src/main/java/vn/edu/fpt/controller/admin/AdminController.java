package vn.edu.fpt.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.service.impl.EventServiceImpl;
import vn.edu.fpt.service.impl.UserServiceImpl;
import vn.edu.fpt.service.impl.security.CustomOAuth2User;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImpl;
    private final EventServiceImpl eventServiceImpl;

    public AdminController(UserServiceImpl userServiceImpl,
                           EventServiceImpl eventServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.eventServiceImpl = eventServiceImpl;
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
        return "admin/DashboardAdmin";
    }


}
