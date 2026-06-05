package vn.edu.fpt.controller.organizer;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/organizer")
public class DashBoardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        return "organizer/DashboardOrganizer";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activeMenu", "profile");
        return "organizer/UpdateProfileOrganizer";
    }




}