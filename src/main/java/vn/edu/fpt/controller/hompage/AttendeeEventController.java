package vn.edu.fpt.controller.hompage;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.EventCategoryService;
import vn.edu.fpt.service.EventService;

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

        Page<Event> eventPage = eventService.searchEvents(criteria, pageable);

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
}