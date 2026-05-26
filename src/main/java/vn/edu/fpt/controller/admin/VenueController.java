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
import vn.edu.fpt.service.VenueService;


@Controller
@RequestMapping("/admin")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("/addvenue")
    public String getCreateVenuePage(Model model
    ) {
        model.addAttribute("CreateVenueDTO", new CreateVenueDTO());
        return "admin/venue/AddVenue";
    }

    @PostMapping("/addvenue")
    public String createVenue(
            @Valid @ModelAttribute("createVenueDTO") CreateVenueDTO request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "redirect:/admin/addvenue";
        }

        venueService.createVenue(request);
        return "admin/DashboardAdmin";
    }

}
