package vn.edu.fpt.controller.moderator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.modelview.request.moderator.CreateOrganizerRequest;
import vn.edu.fpt.service.ModeratorEventListService;
import vn.edu.fpt.service.ModeratorOrganizerService;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorOrganizerController {

    private final ModeratorOrganizerService moderatorOrganizerService;

    @GetMapping("/create")
    public String showCreateOrganizerForm(Model model) {
        model.addAttribute("createOrganizerRequest", new CreateOrganizerRequest());
        model.addAttribute("activePage", "OrganizerManagement");

        return "moderator/CreateOrganizerAccount";
    }

    @PostMapping("/create")
    public String createOrganizer(
            @Valid @ModelAttribute("createOrganizerRequest") CreateOrganizerRequest request,
            BindingResult bindingResult,
            Model model) {

        if (request.getEmail() != null) {
            request.setEmail(request.getEmail().trim());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage",  "OrganizerManagement");

            return "moderator/CreateOrganizerAccount";
        }

        try {
            moderatorOrganizerService.createOrganizerAccount(request);
            model.addAttribute("successMessage", "Tạo tài khoản Organizer thành công.");
            model.addAttribute("createOrganizerRequest", new CreateOrganizerRequest());
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "email.exists", e.getMessage());

            return "moderator/CreateOrganizerAccount";
        }

        model.addAttribute("activePage", "OrganizerManagement");

        return "moderator/CreateOrganizerAccount";

    }
}
