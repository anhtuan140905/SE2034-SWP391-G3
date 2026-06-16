package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.admin.ActivityDTO;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.modelview.request.admin.UpdateUserStatusDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.CityServiceImpl;
import vn.edu.fpt.service.impl.CloudinaryService;
import vn.edu.fpt.service.impl.UserServiceImpl;
import vn.edu.fpt.service.impl.WardServiceImpl;
import vn.edu.fpt.service.impl.security.CustomOAuth2User;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final CityServiceImpl cityServiceImpl;
    private final WardServiceImpl wardServiceImpl;
    private final CloudinaryService cloudinaryService;



    public UserController(UserServiceImpl userServiceImpl, CityServiceImpl cityServiceImpl, WardServiceImpl wardServiceImpl, CloudinaryService cloudinaryService ) {
        this.userServiceImpl = userServiceImpl;
        this.cityServiceImpl = cityServiceImpl;
        this.wardServiceImpl = wardServiceImpl;
        this.cloudinaryService = cloudinaryService;
    }
    @GetMapping("/listuser")
    public String getListUserPage(Model model,
                                  @RequestParam(required = false) String keyword,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  @AuthenticationPrincipal CustomOAuth2User oAuth2Users){

        List<User> users ;
        if(keyword==null||keyword.trim().isEmpty()){
            users = userServiceImpl.getAllUser();
        }
        else {
            users = userServiceImpl.searchUser(keyword);

        }
        model.addAttribute("users", users);
        model.addAttribute("keyword",keyword);


        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        return "admin/user/ListUser";
    }

    @GetMapping("/viewdetailuser")
    public String getViewDetailUserPage(@RequestParam Long id,
                                        Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @AuthenticationPrincipal CustomOAuth2User oAuth2Users){
        User users = userServiceImpl.findById(id);
        model.addAttribute("users",users);

        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        List<ActivityDTO> activities = userServiceImpl.getUserActivities(id);

        String role = users.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getRoleName().name())
                .orElse("UNKNOWN");

        model.addAttribute("activities", activities);
        model.addAttribute("role", role);

        return "admin/user/ViewDetailUser";
    }

    @GetMapping("/edituser")
    public String editUserPage(@RequestParam Long id,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        UpdateUserStatusDTO dto = new UpdateUserStatusDTO();
        List<User> users = userServiceImpl.getAllUser();
        User user = userServiceImpl.findById(id);

        model.addAttribute("UpdateUserStatusDTO", dto);
        model.addAttribute("users", users);
        model.addAttribute("user", user);

        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        RoleName roleName = user.getRoles().iterator().next().getRoleName();

        model.addAttribute("userrole", roleName);
        return "admin/user/EditUser";

    }
    @PostMapping("/edituser")
    public String editUserPage(
            @Valid @ModelAttribute("UpdateUserStatusDTO") UpdateUserStatusDTO dto,
            BindingResult result,
            Model model,
            @RequestParam Long id) {

        if (result.hasErrors()) {
            User editedUser = userServiceImpl.findById(id);
            model.addAttribute("user", editedUser);

            return "admin/user/EditUser";
        }
        else {
            userServiceImpl.updateUser(id,dto);
        }

        return "redirect:/admin/listuser";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {
        User user = new User();
        if(userDetails != null) {
            user = this.userServiceImpl.findByUsername(userDetails.getUsername());
        } else {
            user = this.userServiceImpl.findByUsername(oAuth2Users.getName());
        }
        UpdateAttendeeProfileDTO dto = new UpdateAttendeeProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setDob(user.getDob());
        if(user.getAddress() != null){
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId()));
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }

        model.addAttribute("cities", this.cityServiceImpl.getCityList());
        model.addAttribute("userUpdateDTO", dto);
        return "homepage/UpdateProfileUser";
    }


    @PostMapping("/update/profile")
    public String updateProfile(
            Model model,
            @Valid @ModelAttribute UpdateAttendeeProfileDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());
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
            this.userServiceImpl.handleUpdateUser(dto, result);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        return "redirect:/admin/profile";
    }

}
