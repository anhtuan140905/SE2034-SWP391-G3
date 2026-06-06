package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.organizer.*;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;
import vn.edu.fpt.service.impl.EventServiceImpl;

import java.time.LocalDate;
import java.util.List;
@Controller
//@RestController
@RequestMapping("/organizer")
@AllArgsConstructor
public class EventController {
    private EventServiceImpl eventService;
    private final UserService userService;
    @GetMapping("/create/event")
    public String CreateEvent(Model model,@AuthenticationPrincipal CustomUserDetails userDetails){
        User user = this.userService.findByUsername(userDetails.getUsername());
        OrganizerDTO dto = new OrganizerDTO();
        dto.setOrganizerID(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setDob(user.getDob());
        dto.setGender(user.getGender());
        dto.setAvatar(user.getAvatar());
        if(user.getAddress() != null){
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId()));
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }
        model.addAttribute("organizerdto",dto);
        List<EventCategory> eventCategoryList = eventService.getListEventCategory();
        EventDTO eventDTO = new EventDTO();
        eventDTO.setOrganizerDtoID(dto.getOrganizerID());
        model.addAttribute("eventCategoryList",eventCategoryList);
        model.addAttribute(
                "event",
                eventDTO
        );
        return "organizer/event/CreateOrganizerEvent";
    }

    @PostMapping("create/event")
    public String SaveEvent(@Valid @ModelAttribute("event") EventDTO eventDTO,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "/organizer/create/event";
        }
        eventService.saveEvent(eventDTO);

        return "redirect:/organizer/dashboard";
    }
    @ResponseBody
    @GetMapping("api/venue")
    public  List<VenueDto> getVenueDiffDate(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date){
        return eventService.findByDateNot(date);
    }
    @ResponseBody
    @GetMapping("api/venueid")
    public List<VenueZoneOrganizerDTO> getVenueZonebyVenueId(@RequestParam Long id){
        List<VenueZoneOrganizerDTO> venueZoneDTOS= eventService.getVenueZoneByVenueId(id);
        return  venueZoneDTOS;
    }
    @ResponseBody
    @GetMapping("api/address")
    public VenueDto getAddressbyAddressid(@RequestParam Long venueid){
        return  eventService.getVenuebyId(venueid);
    }
}
