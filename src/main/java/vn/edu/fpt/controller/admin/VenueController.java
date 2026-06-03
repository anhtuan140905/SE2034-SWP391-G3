package vn.edu.fpt.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.VenueService;
import vn.edu.fpt.service.WardService;
import vn.edu.fpt.service.impl.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class VenueController {

    private final VenueServiceImpl venueServiceImpl;
    private final CityServiceImpl cityServiceImpl;
    private final WardServiceImpl wardServiceImpl;
    private final CloudinaryService cloudinaryService;


    public VenueController(VenueServiceImpl venueServiceImpl, CityServiceImpl cityServiceImpl, WardServiceImpl wardServiceImpl, CloudinaryService cloudinaryService) {
        this.venueServiceImpl = venueServiceImpl;
        this.cityServiceImpl = cityServiceImpl;
        this.wardServiceImpl = wardServiceImpl;
        this.cloudinaryService = cloudinaryService;

    }

    @GetMapping("/addvenue")
    public String getCreateVenuePage(Model model) {
        model.addAttribute("CreateVenueDTO", new CreateVenueDTO());
        model.addAttribute("cities", cityServiceImpl.getCityList());
        return "admin/venue/AddVenue";
    }

    @PostMapping("/addvenue")
    public String createVenue(
            @Valid @ModelAttribute("CreateVenueDTO") CreateVenueDTO request,
            BindingResult result,
            Model model,
            @RequestParam(value = "venueFile", required = false) MultipartFile venueFile) {

        if (result.hasErrors()) {
            model.addAttribute("cities", cityServiceImpl.getCityList());
            return "admin/venue/AddVenue";
        }
        try {
            if (venueFile != null && !venueFile.isEmpty()) {
                String imageUrl = this.cloudinaryService.uploadFile(venueFile, "venueFile");
                request.setImageUrl(imageUrl);
            } else {
                request.setImageUrl(null);
            }
            venueServiceImpl.createVenue(request);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());
            return "admin/AddVenue";
        }


        return "redirect:/admin/listvenue";
    }

    @GetMapping("/wards/{cityId}")
    @ResponseBody
    public List<Ward> getWardsByCity(@PathVariable Long cityId) {

        return wardServiceImpl.findByCityId(cityId);
    }

    @GetMapping("/listvenue")
    public String getListVenuePage(Model model,
                                   @RequestParam(required = false) String keyword) {
        List<Venue> venues;
        if (keyword == null || keyword.trim().isEmpty()) {
            venues = venueServiceImpl.getAllVenue();
        } else {
            venues = venueServiceImpl.searchVenue(keyword);
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("venues", venues);
        return "admin/venue/ListVenue";
    }


    @GetMapping("/viewdetailvenue")
    public String getViewDetailVenuePage(@RequestParam Long id, Model model) {
        Venue venues = venueServiceImpl.findById(id);
        model.addAttribute("venues", venues);
        return "admin/venue/ViewDetailVenue";
    }

    @GetMapping("/editvenue")
    public String editVenuePage(@RequestParam Long id, Model model) {
        Venue venues = venueServiceImpl.findById(id);

        model.addAttribute("venues", venues);
        model.addAttribute("CreateVenueDTO", new CreateVenueDTO());
        model.addAttribute("cities", cityServiceImpl.getCityList());
        model.addAttribute("zones", venues.getZones());
        return "admin/venue/EditVenue";
    }

    @PostMapping("/editvenue")
    public String editVenuePage(
            @Valid @ModelAttribute("CreateVenueDTO") CreateVenueDTO request,
            BindingResult result,
            Model model,
            @RequestParam(value = "venueFile", required = false) MultipartFile venueFile,
            @RequestParam Long id

    ) {


        if (result.hasErrors()) {
            model.addAttribute("cities", cityServiceImpl.getCityList());
            return "admin/venue/EditVenue";
        }
        try {
            if (venueFile != null && !venueFile.isEmpty()) {
                String imageUrl = this.cloudinaryService.uploadFile(venueFile, "venueFile");
                request.setImageUrl(imageUrl);
            } else {
                request.setImageUrl(null);
            }
            venueServiceImpl.createVenue(request);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());

            return "admin/EditVenue";
        }

        return "redirect:/admin/listvenue";
    }

}