package vn.edu.fpt.controller.moderator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.service.ModeratorOrganizerInformationService;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorOrganizerInformationController {

    private final ModeratorOrganizerInformationService moderatorOrganizerInformationService;

    @GetMapping("/information/{id}")
    public String organizerInformation(@PathVariable Long id,
                                       @RequestParam(value = "fromEvent", required = false) Long fromEvent,
                                       Model model) {

        model.addAttribute("organizer", moderatorOrganizerInformationService.getOrganizerInformation(id));
        model.addAttribute("backEventId", fromEvent);

        return "moderator/OrganizerInformation";
    }

}
