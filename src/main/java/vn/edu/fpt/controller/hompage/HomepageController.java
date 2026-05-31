package vn.edu.fpt.controller.hompage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.CloudinaryService;
import vn.edu.fpt.service.impl.CustomUserDetails;


@Controller
public class HomepageController {
    private final UserService userService;
    private final CityService cityService;
    private final CloudinaryService cloudinaryService;

    public HomepageController(UserService userService, CityService cityService,  CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.cityService = cityService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/")
    public String homepage(
            Model model){
        return "homepage/Home";
    }

    @GetMapping("/events")
    public String getEvents(){
        return "homepage/ListPublicEvents";
    }


    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = this.userService.findByUsername(userDetails.getUsername());

        UpdateAttendeeProfileDTO dto = new UpdateAttendeeProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());

        if(user.getAddress() != null){
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId())); // set ID để JS pre-select
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }

        model.addAttribute("cities", this.cityService.getCityList());
        model.addAttribute("userUpdateDTO", dto);
        return "homepage/UpdateProfileUser";
    }

    @PostMapping("/attendee/update/profile")
    public String updateProfile(
        Model model,
        @Valid @ModelAttribute UpdateAttendeeProfileDTO dto,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        try {
            if(avatarFile != null && !avatarFile.isEmpty()){
                String imageUrl = this.cloudinaryService.uploadFile(avatarFile, "avatars");
                dto.setAvatar(imageUrl);
            } else {
                dto.setAvatar(null);
            }
            this.userService.handleUpdateUser(dto);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        return "redirect:/profile";

    }


}
