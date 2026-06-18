package vn.edu.fpt.controller.moderator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.ModeratorEventListDTO;
import vn.edu.fpt.repository.EventCategoryRepository;
import vn.edu.fpt.service.ModeratorEventListService;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorEventController {

    private final EventCategoryRepository eventCategoryRepository;
    private final ModeratorEventListService moderatorEventListService;

    @GetMapping("/events")
    public String evenList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        EventStatus eventStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                eventStatus = EventStatus.valueOf(status);
            } catch  (IllegalArgumentException e) {
            }
        }

        Page<ModeratorEventListDTO> events =
                moderatorEventListService.getEvents(eventStatus, keyword, categoryId, page, size);
        model.addAttribute("events", events);
        model.addAttribute("eventStatus", moderatorEventListService.getEventStats());
        model.addAttribute("categories", eventCategoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("activePage", events);

        return "moderator/EventManagement";
    }

    @PostMapping("/events/{id}/deactive")
    public String deactivateEvent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ){
        try {
            moderatorEventListService.deactivateEvent(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Sự kiện đã được đóng thành công.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/moderator/events";
    }

}