package vn.edu.fpt.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(){
        return "admin/DashboardAdmin";
    }


}
