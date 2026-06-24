package vn.edu.fpt.controller.moderator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.modelview.request.moderator.CreateOrganizerRequest;
import vn.edu.fpt.modelview.response.moderator.ModeratorOrganizerListDTO;
import vn.edu.fpt.modelview.response.moderator.OrganizerManagementStatsDTO;
import vn.edu.fpt.service.ModeratorOrganizerInformationService;
import vn.edu.fpt.service.ModeratorOrganizerService;

@Controller
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorOrganizerController {

    private final ModeratorOrganizerService moderatorOrganizerService;
    private final ModeratorOrganizerInformationService moderatorOrganizerInformationService;

    @GetMapping("/organizer")
    public String organizerList(Model model) {
        return "/moderator/OrganizerManagement";
    }

    @GetMapping("/organizer/create")
    public String showCreateOrganizerForm(Model model) {
        model.addAttribute("createOrganizerRequest", new CreateOrganizerRequest());
        model.addAttribute("activePage", "OrganizerManagement");

        return "moderator/CreateOrganizerAccount";
    }

    @PostMapping("/organizer/create")
    public String createOrganizer(
            @Valid @ModelAttribute("createOrganizerRequest") CreateOrganizerRequest request,
            BindingResult bindingResult,
            Model model) {

        if (request.getEmail() != null) {
            request.setEmail(request.getEmail().trim());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "OrganizerManagement");

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

    @GetMapping("/information/{id}")
    public String organizerInformation(
            @PathVariable Long id,
            @RequestParam(value = "fromEvent", required = false) Long fromEvent,
            Model model) {

        model.addAttribute("organizer", moderatorOrganizerInformationService.getOrganizerInformation(id));
        model.addAttribute("backEventId", fromEvent);

        return "moderator/OrganizerInformation";
    }

    @GetMapping("/organizers")
    public String organizerList(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<ModeratorOrganizerListDTO> organizers = moderatorOrganizerService.getOrganizers(keyword, status, page, 10);

        OrganizerManagementStatsDTO stats = moderatorOrganizerService.getOrganizerManagementStats();

        model.addAttribute("organizers", organizers);
        model.addAttribute("stats", stats);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", organizers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "organizers");

        return "moderator/OrganizerManagement";
    }


}
