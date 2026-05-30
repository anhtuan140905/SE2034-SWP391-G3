package vn.edu.fpt.controller.organizer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizer")
public class DashBoardController {

    @GetMapping //
    public String dashboard(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        return "organizer/DashboardOrganizer";
    }
    @GetMapping("/create/event")
    public String Create(){
        return "organizer/event/CreateOrganizerEvent";
    }





}