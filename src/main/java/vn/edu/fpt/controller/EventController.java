package vn.edu.fpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.service.impl.EventServiceImpl;

@Controller
@RequestMapping("/organizer")
public class EventController {
    private EventServiceImpl eventService;
    @GetMapping("/create/event")
    public String CreateEvent(Model model){
        EventDTO eventDTO = new EventDTO();
        model.addAttribute(eventDTO);
        return "organizer/event/CreateOrganizerEvent";
    }
    @PostMapping("create/event")
    public String SaveEvent(){

        return "organizer/DashboardOrganizer";
    }
//    @ResponseBody
//    @GetMapping("api/venue")
//    public
}
