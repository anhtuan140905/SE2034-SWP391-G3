package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.modelview.response.organizer.EventCardDTO;
import vn.edu.fpt.modelview.response.organizer.EventDetailDTO;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;
import vn.edu.fpt.service.impl.EventServiceImpl;

import java.util.Arrays;
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
    public String CreateEvent(Model model,@AuthenticationPrincipal CustomUserDetails userDetails){
        List<EventCategory> eventCategoryList = eventService.getListEventCategory();
        List<cityDto> listCity = eventService.getListcity();
        OrganizerProfile o =  eventService.GetOrganizerProfileByUserId(userDetails.getUser().getId());
        Long userId = userDetails.getUser().getId();
        model.addAttribute("eventCategoryList",eventCategoryList);
        model.addAttribute("citys",listCity);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setOrganizerId(userId);
        boolean hasOrganizerProfile = o != null;
        model.addAttribute(
                "hasOrganizerProfile",
                hasOrganizerProfile
        );
        if (!hasOrganizerProfile) {
            model.addAttribute(
                    "organizerProfile",
                    new OrganizerProfileDto()
            );
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
        boolean hasOrganizerProfile = eventService.GetOrganizerProfileByUserId(userId) != null;
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
    public List<wardDTO> listWard(@RequestParam Long cityId){
        return eventService.listWardDtos(cityId);
    }
//    sửa lại lấy theo OrnizerMember
    @GetMapping("list/event")
    public String ListEvent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "status", required = false) String[] statuses,
            @RequestParam(defaultValue = "")                  String   keyword,
            @RequestParam(defaultValue = "1")                 int      page,
            Model model){
        model.addAttribute("activeMenu", "listevent");
        if (statuses == null || statuses.length == 0) {
            statuses = new String[]{};
        }
        List<String> selectedStatuses = Arrays.asList(statuses);
        Long organizerId = userDetails.getUser().getId();
        Page<EventCardDTO> pageResult = eventService.getEventCards(organizerId,statuses, keyword, page);
        model.addAttribute("eventCards",        pageResult.getContent());
        model.addAttribute("currentPage",       pageResult.getNumber() + 1);
        model.addAttribute("totalPages",        pageResult.getTotalPages());
        model.addAttribute("totalItems",        pageResult.getTotalElements());

        long from = pageResult.getTotalElements() == 0 ? 0
                : (long) pageResult.getNumber() * pageResult.getSize() + 1;
        long to   = Math.min(from + pageResult.getSize() - 1,
                pageResult.getTotalElements());
        model.addAttribute("fromIndex",         from);
        model.addAttribute("toIndex",           to);

        model.addAttribute("selectedStatuses",  selectedStatuses); // ["APPROVED","ENDED"]
        model.addAttribute("keyword",           keyword);
        return "organizer/event/MyEvent";
    }


    @GetMapping("/event/{id}")
    public String getEventDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,Model model){
        if(!staffService.checkPermission(userDetails.getUser().getId(),id,"CAN_VIEW_EDIT_EVENT")){
            return "organizer/DashboardOrganizer";
        }
        EventDetailDTO eventDetailDTO = eventService.getEventDetailById(id);
        model.addAttribute("event",eventDetailDTO);
        return "organizer/event/ViewOrganizerEvent"    ;
    }


}