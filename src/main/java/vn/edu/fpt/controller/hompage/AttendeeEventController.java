package vn.edu.fpt.controller.hompage;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.FavouriteEvent;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;
import vn.edu.fpt.modelview.response.homepage.EventSearchResultDTO;
import vn.edu.fpt.modelview.response.homepage.RecommendationDTO;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.impl.ai.RecommendationService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class AttendeeEventController {
    private final EventService eventService;
    private final EventCategoryService eventCategoryService;
    private final CityService cityService;
    private final FavouriteEventService favouriteEventService;
    private final UserService userService;
    private final RecommendationService recommendationService;
    @GetMapping("/events")
    public String listEvents(
            @PageableDefault(size = 9, sort = "startTime") Pageable pageable,
            EventSearchCriteria criteria,
            Model model) {

        List<String> dynamicMonths = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < 4; i++) {
            dynamicMonths.add(LocalDateTime.now().plusMonths(i).format(formatter));
        }

        Page<EventSearchResultDTO> eventPage = eventService.searchEvents(criteria, pageable);

        model.addAttribute("eventPage", eventPage);
        model.addAttribute("criteria", criteria);
        model.addAttribute("dynamicMonths", dynamicMonths);
        model.addAttribute("categories", this.eventCategoryService.listEventCategories());
//        model.addAttribute("cities", this.cityService.getListCityHaveApprovedEvents());
        return "homepage/ListPublicEvents";
    }

    @GetMapping("/events/detail/{id}")
    public String viewDetailEvent(@PathVariable long id, Model model) {
        EventSummaryProjection event = this.eventService.findEventDetailById(id);
        model.addAttribute("event", event);
        return "homepage/ViewPubliclEvent";
    }

    @GetMapping("/favourites")
    public String getFavouriteEvents(Model model, @AuthenticationPrincipal AuthenticatedUser userDetails) {

        List<EventHomeDTO> favouriteEvents = this.favouriteEventService.findAllByUserId(userDetails.getUser().getId());
        model.addAttribute("favoriteEvents", favouriteEvents);
        return "homepage/favouriteEvent";
    }

    @PostMapping("/api/favourites/toggle")
    @ResponseBody
    public ResponseEntity<?> addFavouriteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập!");
        }
        try {
            String username = authentication.getName();
            User user = this.userService.findByUsername(username);
            if(user == null) {
                return ResponseEntity.status(401).body("Yêu cầu đăng nhập!");
            }
            FavouriteEvent fe = this.favouriteEventService.handleAddEventToFavouriteEvent(user.getId(), eventId);
            if(fe == null) {
                return ResponseEntity.status(500).body("Sự kiện đã tồn tại trong list yêu thích!");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi" + e.getMessage());

        }
    }

    @GetMapping("/recommendation")
    public String recommendationPage(
            @AuthenticationPrincipal AuthenticatedUser userDetails,
            Model model) {
        if(userDetails == null) {
            return "redirect:/auth/login";
        }
        return "homepage/Recommendation";
    }

    @GetMapping("/api/recommendations")
    @ResponseBody
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(
            @AuthenticationPrincipal AuthenticatedUser userDetails) {

        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RecommendationDTO> recommendations = this.recommendationService.getRecommendations(userDetails.getUser().getId());

        return ResponseEntity.ok(recommendations);
    }
}