package vn.edu.fpt.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.VenueService;
import vn.edu.fpt.service.WardService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class VenueController {

    private final VenueService venueService;
    private final CityService cityService;
    private final WardService wardService;

    public VenueController(VenueService venueService, CityService cityService, WardService wardService) {
        this.venueService = venueService;
        this.cityService  = cityService;
        this.wardService  = wardService;
    }

    @GetMapping("/addvenue")
    public String getCreateVenuePage(Model model) {
        model.addAttribute("CreateVenueDTO", new CreateVenueDTO());
        model.addAttribute("cities", cityService.getCityList());
        return "admin/venue/AddVenue";
    }

    @PostMapping("/addvenue")
    public String createVenue(
            @Valid @ModelAttribute("CreateVenueDTO") CreateVenueDTO request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("cities", cityService.getCityList());
            return "admin/venue/AddVenue";
        }

        venueService.createVenue(request);
        return "redirect:/admin/listvenue";
    }

    @GetMapping("/wards/{cityId}")
    @ResponseBody
    public List<Ward> getWardsByCity(@PathVariable Long cityId) {

        return wardService.findByCityId(cityId);
    }

    @GetMapping("/listvenue")
    public String getListVenuePage(Model model) {
        List<Venue> venues = venueService.getAllVenue();
        model.addAttribute("venues", venues);
        return "admin/venue/ListVenue";
    }


    @GetMapping("/viewdetailvenue")
    public String getViewDetailVenuePage(@RequestParam Long id, Model model) {
        Venue venues = venueService.findById(id);
        model.addAttribute("venues", venues);
        return "admin/venue/ViewDetailVenue";
    }

    @GetMapping("/editvenue")
    public String editVenuePage(Model model) {
        List<Venue> venues = venueService.getAllVenue();
        model.addAttribute("venues", venues);
        return "admin/venue/EditVenue";
    }
}