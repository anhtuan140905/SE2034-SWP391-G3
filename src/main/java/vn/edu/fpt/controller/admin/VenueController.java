package vn.edu.fpt.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.VenueService;
import vn.edu.fpt.service.WardService;
import vn.edu.fpt.service.impl.CloudinaryService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class VenueController {

    private final VenueService venueService;
    private final CityService cityService;
    private final WardService wardService;
    private final CloudinaryService cloudinaryService;

    public VenueController(VenueService venueService, CityService cityService, WardService wardService, CloudinaryService cloudinaryService) {
        this.venueService = venueService;
        this.cityService = cityService;
        this.wardService = wardService;
        this.cloudinaryService = cloudinaryService;
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
            Model model,
            @RequestParam(value = "venueFile", required = false) MultipartFile venueFile) {

        if (result.hasErrors()) {
            model.addAttribute("cities", cityService.getCityList());
            return "admin/venue/AddVenue";
        }
        try {
            if (venueFile != null && !venueFile.isEmpty()) {
                String imageUrl = this.cloudinaryService.uploadFile(venueFile, "venueFile");
                request.setImageUrl(imageUrl);
            } else {
                request.setImageUrl(null);
            }
            venueService.createVenue(request);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityService.getCityList());
            return "admin/AddVenue";
        }


        return "redirect:/admin/listvenue";
    }

    @GetMapping("/wards/{cityId}")
    @ResponseBody
    public List<Ward> getWardsByCity(@PathVariable Long cityId) {

        return wardService.findByCityId(cityId);
    }

    @GetMapping("/listvenue")
    public String getListVenuePage(Model model,
                                   @RequestParam(required = false) String keyword) {
        List<Venue> venues;
        if (keyword==null||keyword.trim().isEmpty()) {
            venues = venueService.getAllVenue();
        }
        else {
             venues = venueService.searchVenue(keyword);
        }
model.addAttribute("keyword", keyword);
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