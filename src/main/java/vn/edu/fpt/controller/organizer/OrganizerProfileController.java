package vn.edu.fpt.controller.organizer;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.exception.ProfileNotFoundException;
import vn.edu.fpt.modelview.request.organizer.OrganizerProfileDto;
import vn.edu.fpt.security.CustomUserDetails;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.OrganizerProfileService;

@Controller
@RequestMapping("/organizer")
@AllArgsConstructor
public class OrganizerProfileController {

    private final OrganizerProfileService organizerProfileService;
    private final EventService eventService;

    @GetMapping("/profile")
    public String viewProfileOrganizer(Model model, @AuthenticationPrincipal CustomUserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        Long userId = userDetails.getUser().getId();
        OrganizerProfileDto dto;
        try {
            dto = organizerProfileService.getOrganizerProfileByUserId(userId);
        } catch (ProfileNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
            return "redirect:/organizer/list/event";

        }
        model.addAttribute("organizerProfile", dto);
        model.addAttribute("banks", eventService.getListBank());
        return "organizer/UpdateProfileOrganizer";
    }

    @PostMapping("/profile")
    public String updateProfileOrganizer(
            @Valid @ModelAttribute("organizerProfile") OrganizerProfileDto dto,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("banks", eventService.getListBank());
            return "organizer/UpdateProfileOrganizer";
        }

        Long userId = userDetails.getUser().getId();
        
        organizerProfileService.updateProfile(userId, dto);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        return "redirect:/organizer/profile";
    }
}
