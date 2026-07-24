package vn.edu.fpt.controller.hompage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
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
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.security.CustomUserDetails;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.impl.*;
import vn.edu.fpt.security.CustomOAuth2User;


import java.util.List;

@Controller
@AllArgsConstructor
public class HomepageController {
    private final UserServiceImpl userServiceImpl;
    private final UserService userService;
    private final CityService cityService;
    private final CloudinaryService cloudinaryService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final EventCategoryServiceImpl eventCategoryService;
    private final FavouriteEventService favouriteEventService;

    @GetMapping("/")
    public String homepage(
            Model model){
        long hostedEvents = this.eventService.countHostedEvents();
        model.addAttribute("hostedEvents", hostedEvents);
        long issuedTickets = this.ticketService.issuedTickets();
        model.addAttribute("issuedTickets", issuedTickets);
        long eventCategories = this.eventCategoryService.countEventCategories();
        model.addAttribute("eventCategories", eventCategories);
        long activatedOrganizer = this.userServiceImpl.getActivatedOrganizers().size();
        model.addAttribute("activatedOrganizers", activatedOrganizer);
        List<EventSummaryDto> featuredEvents = this.eventService.findTopFeaturedEvents();
        model.addAttribute("featuredEvents", featuredEvents);
        List<FeaturedOrganizerDto> featuredOrganizers = this.userServiceImpl.getFeaturedOrganizers();
        model.addAttribute("featuredOrganizers", featuredOrganizers);
        FeaturedEventDTO featuredEvent = this.eventService.findFeaturedEvent();
        model.addAttribute("featuredEvent", featuredEvent);
        return "homepage/Home";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal AuthenticatedUser userDetails,
                             @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        String email = (userDetails != null) ? userDetails.getUser().getEmail() : oAuth2Users.getName();
        UpdateAttendeeProfileDTO dto = this.userService.getProfileDTOByEmail(email);

        this.populateProfileStats(model, email);

        model.addAttribute("cities", this.cityService.getCityList());
        model.addAttribute("userUpdateDTO", dto);

        return "homepage/UpdateProfileUser";
    }

    private void populateProfileStats(Model model, String email) {
        User user = this.userService.findByUsername(email);
        if (user != null) {
            Long ticketCount = this.ticketService.countAllTicketOfUser(user.getId());
            Long attendedCount = this.eventService.countAttendedEvent(user.getId());
            long favCount = 0;
            if (this.favouriteEventService.checkUserHaveFavouriteEvent(user.getId())) {
                favCount = this.favouriteEventService.findAllByUserId(user.getId()).size();
            }
            model.addAttribute("ticketCount", ticketCount);
            model.addAttribute("attendedCount", attendedCount);
            model.addAttribute("favCount", favCount);
        }
        model.addAttribute("cities", this.cityService.getCityList());
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
            }
            this.userServiceImpl.handleUpdateUser(dto, result);
            if (result.hasErrors()) {
                model.addAttribute("cities", this.cityService.getCityList());
                model.addAttribute("userUpdateDTO", dto);

                return "homepage/UpdateProfileUser";
            }
        } catch (Exception e) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("userUpdateDTO", dto);

            return "homepage/UpdateProfileUser";
        }
        return "redirect:/profile";
    }

    @GetMapping("/my-tickets")
    public String getViewOwnTicket(Model model,
                                   Authentication authentication,
                                   @RequestParam(defaultValue = "all") String tab) {


        String email = authentication.getName();


        User user = userServiceImpl.findByUsername(email);
        Long userId = user.getId();

        Long countTicket = ticketService.countAllTicketOfUser(userId);
        model.addAttribute("countTicket", countTicket);

        Long countUpcomingEvent = eventService.countUpcomingEvent(userId);
        model.addAttribute("countUpcomingEvent", countUpcomingEvent);

        Long countAttendedEvent = eventService.countAttendedEvent(userId);
        model.addAttribute("countAttendedEvent", countAttendedEvent);

        Long countUpcomingTicket = ticketService.countUpcomingTicket(userId);
        model.addAttribute("countUpcomingTicket", countUpcomingTicket);

        Long countUsedTicket = ticketService.countUsedTicket(userId);
        model.addAttribute("countUsedTicket", countUsedTicket);

        Long countExpiredTicket = ticketService.countExpiredTicket(userId);
        model.addAttribute("countExpiredTicket", countExpiredTicket);


        List<TicketDTO> viewTicket = ticketService.viewTicket(userId, tab);
        model.addAttribute("viewTicket", viewTicket);

        model.addAttribute("tab", tab);
        model.addAttribute("user", user);



        return "homepage/ViewOwnTicket";
    }
    @GetMapping("/my-detailtickets")
    public String getViewDetailTicket(@RequestParam Long ticketId,
                                      Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {


        try{
            User currentUser = (userDetails != null)
                    ? userService.findByUsername(userDetails.getUsername())
                    : userService.findByUsername(oAuth2Users.getName());
            model.addAttribute("currentUser", currentUser);
            TicketDTO ticketDetail = ticketService.viewDetailTicket(ticketId, currentUser.getId());

            model.addAttribute("ticketDetail", ticketDetail);
            return "homepage/ViewDetailTicket";
        }
        catch (Exception e){
            return "redirect:/my-tickets";
        }

    }
}