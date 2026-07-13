package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.admin.ActivityDTO;
import vn.edu.fpt.modelview.request.admin.UpdateUserStatusDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.impl.CloudinaryService;
import vn.edu.fpt.security.CustomOAuth2User;
import vn.edu.fpt.security.CustomUserDetails;
import vn.edu.fpt.service.impl.UserServiceImpl;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class UserController {
    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final EventCategoryService eventCategoryService;
    private final UserServiceImpl userServiceImpl;


    public UserController(UserService userService, EventService eventService, TicketService ticketService, EventCategoryService eventCategoryService, UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.eventCategoryService = eventCategoryService;
        this.userServiceImpl = userServiceImpl;

    }

    @GetMapping("/listuser")
    public String getListUserPage(Model model,
                                  @RequestParam(required = false) String keyword,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {

        try {
            User currentUser = (userDetails != null)
                    ? userService.findByUsername(userDetails.getUsername())
                    : userService.findByUsername(oAuth2Users.getName());
            model.addAttribute("currentUser", currentUser);
            List<User> users;
            if (keyword == null || keyword.trim().isEmpty()) {
                users = userService.getAllUser();
            } else {
                users = userService.searchUser(keyword);

            }
            model.addAttribute("users", users);
            model.addAttribute("keyword", keyword);


            return "admin/user/ListUser";
        } catch (Exception e) {

            return "redirect:/admin/listuser";
        }
    }

    @GetMapping("/viewdetailuser")
    public String getViewDetailUserPage(@RequestParam Long id,
                                        Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        User users = userService.findById(id);
        model.addAttribute("users", users);

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        List<ActivityDTO> activities = userService.getUserActivities(id);

        String role = users.getUserRoles().stream()
                .findFirst()
                .map(ur -> ur.getRole().getRoleName().name())
                .orElse("UNKNOWN");

        model.addAttribute("activities", activities);
        model.addAttribute("role", role);

        return "admin/user/ViewDetailUser";
    }

    @GetMapping("/edituser")
    public String editUserPage(@RequestParam Long id,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        UpdateUserStatusDTO dto = new UpdateUserStatusDTO();
        List<User> users = userService.getAllUser();
        User user = userService.findById(id);

        model.addAttribute("UpdateUserStatusDTO", dto);
        model.addAttribute("users", users);
        model.addAttribute("user", user);

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        RoleName roleName = user.getUserRoles().iterator().next().getRole().getRoleName();

        model.addAttribute("roleName", roleName);
        return "admin/user/EditUser";

    }

    @PostMapping("/edituser")
    public String editUserPage(
            @Valid @ModelAttribute("dto") UpdateUserStatusDTO dto,
            BindingResult result,
            Model model,
            @RequestParam Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {

        if (result.hasErrors()) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("dto", dto);
            return "admin/user/EditUser";
        }

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());

        userService.updateUser(id, dto, currentUser.getId());

        return "redirect:/admin/listuser";
    }

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



}