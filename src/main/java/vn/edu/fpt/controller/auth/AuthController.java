package vn.edu.fpt.controller.auth;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.modelview.request.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.RegisterUserDTO;
import vn.edu.fpt.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String getLoginPage() {
        return "auth/Login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        model.addAttribute("registerOrgDTO", new RegisterOrgDTO());
        return "auth/RegisterAccount";
    }

    @PostMapping("/register/user")
    public String registerUser(
            @Valid RegisterUserDTO dto,
            BindingResult result) {
        if(result.hasErrors()) {
            return "auth/RegisterAccount";
        }
        this.userService.handleCreateUser(dto);
        return "auth/Login";
    }


}
