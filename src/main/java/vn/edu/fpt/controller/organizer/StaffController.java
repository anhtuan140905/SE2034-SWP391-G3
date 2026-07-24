package vn.edu.fpt.controller.organizer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.security.CustomUserDetails;

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
                             @AuthenticationPrincipal AuthenticatedUser userDetails,
                             Model model) {
        if(!staffService.checkPermission(userDetails.getUser().getId(),id,"MANAGER_STAFF_MANAGE")){
            return "organizer/Forbidden";
        }
        Page<StaffResponceDTO> staffPage = staffService.getStaffbyEventID(id, keyword, roleId, page);
        if (!model.containsAttribute("inviteMember")) {
            model.addAttribute("inviteMember", new MemberRequestDTO());
        }
        model.addAttribute("staffList", staffPage);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("eventId", id);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("emailCurrentUser",userDetails.getEmail());
        model.addAttribute("inviteMember", new MemberRequestDTO());
        model.addAttribute("permissions", staffService.getListPermission(userDetails.getUser().getId(),id));
        model.addAttribute("roles", staffService.getRoleOfEvent(userDetails.getUser().getId(),id));
        return "organizer/staff/ListStaffs";
    }
    @PostMapping("/event/{id}/members/invite")
    public String InviteMember(@PathVariable Long id,
                               @Valid @ModelAttribute("inviteMember") MemberRequestDTO memberRequestDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal AuthenticatedUser userDetails
                               ){
        if(!staffService.checkPermission(userDetails.getUser().getId(),id,"MANAGER_STAFF_MANAGE")){
            return "organizer/Forbidden";
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
            @RequestParam Long staffId,
            @RequestParam int currentPage,
            Model model,@AuthenticationPrincipal AuthenticatedUser userDetails
            ,RedirectAttributes redirectAttributes) {
        if(!staffService.checkPermission(userDetails.getUser().getId(),eventId,"MANAGER_STAFF_MANAGE")){
            return "organizer/Forbidden";
        }
        if(staffService.compareRole(userDetails.getUserId(),staffId,eventId)){
            redirectAttributes.addFlashAttribute("errorDelete","Bạn không có quyền sửa nhần viên này");
            return "redirect:/organizer/event/" + eventId + "/members";
        }
        model.addAttribute("selectedMember",staffService.getInfobyStaffID(staffId));
        Page<StaffResponceDTO> staffPage = staffService.getStaffbyEventID(eventId, "", null, currentPage);
        model.addAttribute("staffList", staffPage.getContent());
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("emailCurrentUser",userDetails.getEmail());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("eventId", eventId);
        model.addAttribute("keyword", "");
        model.addAttribute("roleId", null);
        model.addAttribute("inviteMember", new MemberRequestDTO());
        model.addAttribute("permissions", staffService.getListPermission(userDetails.getUser().getId(),eventId));
        model.addAttribute("roles", staffService.getRoleOfEvent(userDetails.getUser().getId(),eventId));
        return "organizer/staff/ListStaffs";
    }
    @PostMapping("/event/{eventId}/members/update")
    public String updateMenber(@PathVariable Long eventId,
                               @Valid @ModelAttribute("selectedMember") StaffDetailDto staffDetailDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal AuthenticatedUser userDetails ){
        if(!staffService.checkPermission(userDetails.getUser().getId(),eventId,"MANAGER_STAFF_MANAGE")){
            return "organizer/Forbidden";
        }
        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessageEdit", errorMsg);
            redirectAttributes.addFlashAttribute("selectedMember", staffDetailDto);
            return "redirect:/organizer/event/" + eventId + "/members";
        }
        try {
            staffService.updateStaff(userDetails.getUserId(),staffDetailDto,eventId);
        }catch (RuntimeException e){

            redirectAttributes.addFlashAttribute("errorDelete", e.getMessage());
        }
        return "redirect:/organizer/event/" + eventId + "/members";
    }
    @GetMapping("/event/{eventId}/members/delete")
    public String delete(@RequestParam Long staffId,
                         @PathVariable Long eventId,
                         @AuthenticationPrincipal AuthenticatedUser userDetails,
                         RedirectAttributes redirectAttributes){
        if(!staffService.checkPermission(userDetails.getUser().getId(),eventId,"MANAGER_STAFF_MANAGE")){
            return "organizer/Forbidden";
        }
        try {
            staffService.deleteStaffByStaffId(staffId,eventId,userDetails.getUserId());
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("errorDelete",e.getMessage());
        }
        return "redirect:/organizer/event/" + eventId + "/members";
    }
}
