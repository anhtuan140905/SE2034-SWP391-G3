package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.common.error.ResourceNotFoundException;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.modelview.response.organizer.EventCardDTO;
import vn.edu.fpt.modelview.response.organizer.EventDetailDTO;
import vn.edu.fpt.modelview.response.organizer.EventEditDTO;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.security.CustomUserDetails;
import vn.edu.fpt.service.impl.EventServiceImpl;

import java.util.List;
@Controller
//@RestController
@RequestMapping("/organizer")
@AllArgsConstructor
public class EventController {
    private EventServiceImpl eventService;
    private StaffService staffService;
    private final UserService userService;

    @GetMapping("/create/event")
    public String CreateEvent(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<EventCategory> eventCategoryList = eventService.getListEventCategory();
        List<cityDto> listCity = eventService.getListcity();
        Long userId = userDetails.getUser().getId();
        model.addAttribute("eventCategoryList", eventCategoryList);
        model.addAttribute("citys", listCity);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setOrganizerId(userId);
        boolean hasOrganizerProfile = eventService.GetOrganizerProfileByUserId(userDetails.getUser().getId());
        model.addAttribute("hasOrganizerProfile", hasOrganizerProfile);
        if (!hasOrganizerProfile) {
            model.addAttribute("organizerProfile", new OrganizerProfileDto());
        }
        model.addAttribute("event", eventDTO);
        return "organizer/event/CreateOrganizerEvent";
    }

    @PostMapping("create/event")
    public String saveEvent(
            @Valid @ModelAttribute("event") EventDTO eventDTO,
            BindingResult eventResult,
            @ModelAttribute("organizerProfile")
            OrganizerProfileDto organizerProfileDto,
            BindingResult organizerResult,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        // kiểm tra đã có profile chưa
        boolean hasOrganizerProfile = eventService.GetOrganizerProfileByUserId(userId);
        // validate lỗi
        if (eventResult.hasErrors() || (!hasOrganizerProfile && organizerResult.hasErrors())) {
            model.addAttribute("hasOrganizerProfile", hasOrganizerProfile);
            model.addAttribute("eventCategoryList", eventService.getListEventCategory());
            model.addAttribute("citys", eventService.getListcity());
            model.addAttribute("event", eventDTO);
            // chỉ add organizerProfile nếu chưa có
            if (!hasOrganizerProfile) {
                model.addAttribute("organizerProfile", organizerProfileDto);
            }
            return "organizer/event/CreateOrganizerEvent";
        }
        // tránh client sửa organizerId
        eventDTO.setOrganizerId(userId);
        // save
        if (!hasOrganizerProfile) {
            eventService.saveEvent(eventDTO, organizerProfileDto);
        } else {
            eventService.saveEvent(eventDTO, null);
        }
        return "redirect:/organizer/dashboard";
    }

    @ResponseBody
    @GetMapping("/api/city")
    public List<wardDTO> listWard(@RequestParam Long cityId) {
        return eventService.listWardDtos(cityId);
    }

    //    sửa lại lấy theo OrnizerMember
    @GetMapping("list/event")
    public String ListEvent(
            @AuthenticationPrincipal AuthenticatedUser userDetails,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("message", "Đăng nhập lỗi vui lòng đăng nhập thử bằng tài khoản và mật khẩu");
            return "redirect:/auth/login";
        }
        model.addAttribute("activeMenu", "listevent");

        Long organizerId = userDetails.getUser().getId();
        Page<EventCardDTO> pageResult = eventService.getEventCards(organizerId, status, keyword, page);
        model.addAttribute("eventCards", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber() + 1);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());

        long from = pageResult.getTotalElements() == 0 ? 0
                : (long) pageResult.getNumber() * pageResult.getSize() + 1;
        long to = Math.min(from + pageResult.getSize() - 1,
                pageResult.getTotalElements());
        model.addAttribute("fromIndex", from);
        model.addAttribute("toIndex", to);

        model.addAttribute("selectedStatus", status); // "ACTIVE" hoặc null
        model.addAttribute("keyword", keyword);
        return "organizer/event/MyEvent";
    }


    @GetMapping("/event/{id}")
    public String getEventDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
//        if (!staffService.checkPermission(userDetails.getUser().getId(), id, "CAN_VIEW_EDIT_EVENT")) {
//            return "organizer/DashboardOrganizer";
//        }
        EventDetailDTO eventDetailDTO = eventService.getEventDetailById(id);
        model.addAttribute("event", eventDetailDTO);
        model.addAttribute("eventId", id);

        return "organizer/event/ViewOrganizerEvent";
    }

    @GetMapping("/event/{id}/publish")
    public String publishEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.publishEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng sự kiện thành công!");
        } catch (IllegalStateException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/organizer/list/event";
    }

    @GetMapping("event/{eventId}/edit")
    public String showEditForm(@PathVariable Long eventId,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {

//        try {

        EventEditDTO eventDTO = eventService.UpdateEventById(eventId);

        model.addAttribute("event", eventDTO);
        model.addAttribute("eventCategoryList", eventService.getListEventCategory());
        model.addAttribute("citys", eventService.getListcity());
        return "organizer/event/EditOrganizerEvent";

//        } catch (EventNotEditableException | RuntimeException ex) {
//            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
//            return "redirect:/organizer/dashboard";
//        }
    }

    @PostMapping("event/{eventId}/edit")
    public String updateEvent(
            @PathVariable Long eventId,
            @Valid @ModelAttribute("event") EventEditDTO eventDTO,
            BindingResult eventResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails) {


        if (eventResult.hasErrors()) {
            model.addAttribute("eventCategoryList", eventService.getListEventCategory());
            model.addAttribute("citys", eventService.getListcity());
            model.addAttribute("event", eventDTO);
            return "organizer/event/EditOrganizerEvent";
        }
        eventDTO.setEventId(eventId);
            eventService.updateEvent(eventDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sự kiện thành công.");
        return "redirect:/organizer/dashboard";
    }
}