package vn.edu.fpt.controller.auth;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.common.error.CheckDuplicateException;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.WardService;
import vn.edu.fpt.service.impl.resetPassword.PasswordResetService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final WardService wardService;
    private final CityService cityService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService,
                          WardService wardService,
                          CityService cityService,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.wardService = wardService;
        this.cityService = cityService;
        this.passwordResetService = passwordResetService;
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

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/ForgotPassword";
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendOtp(
            @RequestParam String email,
            HttpSession session) {
        try {
            passwordResetService.sendOtp(email, session);
            // Luôn trả success (không lộ email có tồn tại không)
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (MessagingException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Gửi email thất bại, vui lòng thử lại."));
        }
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String otp,
            HttpSession session) {
        if (!passwordResetService.verifyOtp(otp, session)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mã OTP không đúng hoặc đã hết hạn."));
        }
        session.setAttribute("otp_verified", true);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu xác nhận không khớp."));
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu phải có ít nhất 8 ký tự."));
        }
        if (!Boolean.TRUE.equals(session.getAttribute("otp_verified"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phiên làm việc hết hạn, vui lòng thử lại."));
        }
        boolean success = passwordResetService.resetPassword(newPassword, session);
        if (!success) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phiên làm việc hết hạn, vui lòng thử lại."));
        }
        session.removeAttribute("otp_verified");
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
