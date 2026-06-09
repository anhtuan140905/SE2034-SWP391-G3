package vn.edu.fpt.controller.hompage;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.TicketService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.CloudinaryService;
import vn.edu.fpt.service.impl.security.CustomOAuth2User;
import vn.edu.fpt.service.impl.security.CustomUserDetails;
import vn.edu.fpt.service.impl.EventCategoryServiceImpl;

import java.util.List;


@Controller
@AllArgsConstructor
public class HomepageController {
    private final UserService userService;
    private final CityService cityService;
    private final CloudinaryService cloudinaryService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final EventCategoryServiceImpl eventCategoryService;

    @GetMapping("/")
    public String homepage(
            Model model){
        long hostedEvents = this.eventService.countHostedEvents();
        model.addAttribute("hostedEvents", hostedEvents);
        long issuedTickets = this.ticketService.issuedTickets();
        model.addAttribute("issuedTickets", issuedTickets);
        long eventCategories = this.eventCategoryService.countEventCategories();
        model.addAttribute("eventCategories", eventCategories);
        long activatedOrganizer = this.userService.getActivatedOrganizers().size();
        model.addAttribute("activatedOrganizers", activatedOrganizer);
        List<EventSummaryDto> featuredEvents = this.eventService.findTopFeaturedEvents();
        model.addAttribute("featuredEvents", featuredEvents);
        List<FeaturedOrganizerDto> featuredOrganizers = this.userService.getFeaturedOrganizers();
        model.addAttribute("featuredOrganizers", featuredOrganizers);
        FeaturedEventDTO featuredEvent = this.eventService.findFeaturedEvent();
        model.addAttribute("featuredEvent", featuredEvent);
        return "homepage/Home";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        User user = new User();
        if(userDetails != null) {
            user = this.userService.findByUsername(userDetails.getUsername());
        } else {
            user = this.userService.findByUsername(oAuth2Users.getName());
        }
        UpdateAttendeeProfileDTO dto = new UpdateAttendeeProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setDob(user.getDob());
        if(user.getAddress() != null){
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId()));
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }

        model.addAttribute("cities", this.cityService.getCityList());
        model.addAttribute("userUpdateDTO", dto);
        return "homepage/UpdateProfileUser";
    }


    @PostMapping("/attendee/update/profile")
    public String updateProfile(
        Model model,
        @Valid @ModelAttribute UpdateAttendeeProfileDTO dto,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        try {
            if(avatarFile != null && !avatarFile.isEmpty()){
                String imageUrl = this.cloudinaryService.uploadFile(avatarFile, "avatars");
                dto.setAvatar(imageUrl);
            } else {
                dto.setAvatar(null);
            }
            this.userService.handleUpdateUser(dto, result);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        return "redirect:/profile";
    }


}
