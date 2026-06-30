package vn.edu.fpt.controller.organizer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/organizer")
@AllArgsConstructor
public class StaffController {
    private StaffService staffService;
    @GetMapping("/event/{id}/members")
    public String ListMember(@PathVariable Long id,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(required = false) Long roleId,
                             @RequestParam(defaultValue = "0") int page,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        if(!staffService.checkPermission(userDetails.getUser().getId(),id,5L)){
            return "organizer/DashboardOrganizer";
        }
        int size = 10;
        Page<StaffResponceDTO> staffPage = staffService.getStaffbyEventID(id, keyword, roleId, PageRequest.of(page, size));
        if (!model.containsAttribute("inviteMember")) {
            model.addAttribute("inviteMember", new MemberRequestDTO());
        }
        model.addAttribute("staffList", staffPage);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("eventId", id);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("inviteMember", new MemberRequestDTO());
        model.addAttribute("permissions", staffService.getListPermission());
        model.addAttribute("roles", staffService.getRoleOfEvent());
        return "organizer/staff/ListStaffs";
    }
    @PostMapping("/event/{id}/members/invite")
    public String InviteMember(@PathVariable Long id,
                               @Valid @ModelAttribute("inviteMember") MemberRequestDTO memberRequestDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal CustomUserDetails userDetails
                               ){
        if(!staffService.checkPermission(userDetails.getUser().getId(),id,5L)){
            return "organizer/DashboardOrganizer";
        }
        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("inviteMember", memberRequestDTO);
            return "redirect:/organizer/event/" + id + "/members";
        }

        try{
        staffService.assignMember(memberRequestDTO,id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organizer/event/" + id + "/members";
    }

    @GetMapping("/event/{eventId}/members/edit")
    public String editMember(
            @PathVariable Long eventId,
            @RequestParam Long staffId,   // ← query param không phải path variable
            Model model) {
        model.addAttribute("selectedMember",staffService.getInfobyStaffID(staffId));
        Page<StaffResponceDTO> staffPage = staffService.getStaffbyEventID(eventId, "", null, PageRequest.of(0,10));
        model.addAttribute("staffList", staffPage.getContent());
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("eventId", eventId);
        model.addAttribute("keyword", "");
        model.addAttribute("roleId", null);
        model.addAttribute("inviteMember", new MemberRequestDTO());
        model.addAttribute("permissions", staffService.getListPermission());
        model.addAttribute("roles", staffService.getRoleOfEvent());
        return "organizer/staff/ListStaffs";
    }
    @PostMapping("/event/{eventId}/members/update")
    public String updateMenber(@PathVariable Long eventId,@Valid @ModelAttribute("selectedMember") StaffDetailDto staffDetailDto, BindingResult result){
        if (result.hasErrors()) {
            return "redirect:/organizer/event/members";
        }
        staffService.updateStaff(staffDetailDto);
        return "redirect:/organizer/event/" + eventId + "/members";
    }
}
