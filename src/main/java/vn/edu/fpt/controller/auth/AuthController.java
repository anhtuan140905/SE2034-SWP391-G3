package vn.edu.fpt.controller.auth;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.common.error.CheckDuplicateException;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.WardService;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final WardService wardService;
    private final CityService cityService;

    public AuthController(UserService userService,  WardService wardService, CityService cityService) {
        this.userService = userService;
        this.wardService = wardService;
        this.cityService = cityService;
    }
    @GetMapping("/login")
    public String getLoginPage(
            @RequestParam(value = "error", required = false) String error, Model model
    ) {
        if(error != null) {
            model.addAttribute("loginError", "Tài khoản hoặc mật khẩu không đúng");
        }

        return "auth/Login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("cities", this.cityService.getCityList());
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        model.addAttribute("registerOrgDTO", new RegisterOrgDTO());
        return "auth/RegisterAccount";
    }

    @PostMapping("/register/user")
    public String registerUser(
            @Valid RegisterUserDTO dto,
            BindingResult result,
            Model model) {
        if(result.hasErrors()) {
            model.addAttribute("registerUserDTO", dto);
            model.addAttribute("registerOrgDTO", new  RegisterOrgDTO());
            model.addAttribute("activeRole", "user");
            return "auth/RegisterAccount";
        }
        this.userService.handleCreateUser(dto);
        return "auth/Login";
    }

    @PostMapping("/register/organizer")
    public String registerOrganizer(
            @Valid RegisterOrgDTO dto,
            BindingResult result,
            Model model) {
        if(result.hasErrors()) {
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("registerOrgDTO", dto);
            model.addAttribute("registerUserDTO", new  RegisterUserDTO());
            model.addAttribute("activeRole", "organizer");
            return "auth/RegisterAccount";
        }
        try {
            this.userService.handleCreateOrganizer(dto);

        } catch (CheckDuplicateException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("cities", this.cityService.getCityList());
            model.addAttribute("registerOrgDTO", dto);
            model.addAttribute("registerUserDTO", new  RegisterUserDTO());
            model.addAttribute("activeRole", "organizer");
            return "auth/RegisterAccount";
        }
        return "auth/Login";
    }

    @ResponseBody
    @GetMapping("/api/wards")
    public List<Ward> getWardList(
            @RequestParam Long cityId
    ) {
        return this.wardService.findByCityId(cityId);
    }
}
