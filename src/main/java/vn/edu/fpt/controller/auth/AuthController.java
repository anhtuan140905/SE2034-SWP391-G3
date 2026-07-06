package vn.edu.fpt.controller.auth;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.common.error.ServiceValidationException;
import vn.edu.fpt.model.VerificationToken;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.model.constant.TokenType;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.service.CityService;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.VerifyTokenService;
import vn.edu.fpt.service.WardService;
import vn.edu.fpt.security.PasswordResetService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final WardService wardService;
    private final CityService cityService;
    private final PasswordResetService passwordResetService;
    private final VerifyTokenService  verifyTokenService;


    public AuthController(UserService userService,
                          WardService wardService,
                          CityService cityService,
                          PasswordResetService passwordResetService,
                          VerifyTokenService verifyTokenService
    ) {
        this.userService = userService;
        this.wardService = wardService;
        this.cityService = cityService;
        this.passwordResetService = passwordResetService;
        this.verifyTokenService = verifyTokenService;

    }
    @GetMapping("/login")
    public String getLoginPage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "success", required = false) String success,
            Model model
    ) {

        if(error != null) {
            String msg;
            switch (error) {
                case "not_activated": {
                    msg="Tài khoản chưa được active. Vui lòng check gmail!";
                    break;
                }
                case "locked": {
                    msg="Tài khoản của bạn đã bị khóa";
                    break;
                }
                case "bad_credentials": {
                    msg ="Tài khoản hoặc mật khẩu sai!";
                    break;
                }
                case "fail_active": {
                    msg = "Link kích hoạt không hợp lệ hoặc đã hết hạn!";
                    break;
                }
                default: msg = "Đăng nhập thất bại! Vui lòng thử lại!";
            }
            model.addAttribute("error", msg);
        }
        if(success != null) {
            model.addAttribute("success", "Kích hoạt tài khoản thành công!");
        }

        return "auth/Login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("cities", this.cityService.getCityList());
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        return "auth/RegisterAccount";
    }

    @PostMapping("/register/user")
    public String registerUser(
            @Valid RegisterUserDTO dto,
            BindingResult result,
            Model model) {
        if(result.hasErrors()) {
            model.addAttribute("registerUserDTO", dto);
            return "auth/RegisterAccount";
        }
        try {
            userService.handleCreateUser(dto);
        } catch (ServiceValidationException ex) {
            for (ServiceValidationException.FieldError error : ex.getErrors()) {
                result.rejectValue(error.getField(), "serviceError", error.getMessage());
            }
            return "auth/RegisterAccount";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/activate")
    public String activeOrganizer(
            @RequestParam String token,
            @RequestParam String email) {
        if (this.verifyTokenService.checkActiveAccount(token, email)) {
            return "redirect:/auth/login?success=activate_success";
        }
        return "redirect:/auth/login?error=fail_active";
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
            passwordResetService.sendOtp(email);
        } catch (UsernameNotFoundException e) {
        } catch (MessagingException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Gửi email thất bại, vui lòng thử lại."));
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String otp,
            @RequestParam String email,
            HttpSession session) {
        VerificationToken verificationToken = this.verifyTokenService.findByEmailAndTypeAndUsedFalse(email, TokenType.FORGOT_PASSWORD_OTP);
        if (verificationToken == null || !passwordResetService.verifyOtp(otp, verificationToken)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mã OTP không đúng hoặc đã hết hạn."));
        }
        session.setAttribute("otp_verified", true);
        session.setAttribute("reset_email", email);
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
        try {
            boolean success = passwordResetService.resetPassword(
                    newPassword, confirmPassword, (String) session.getAttribute("reset_email"));
            if (!success) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Phiên làm việc hết hạn, vui lòng thử lại."));
            }
        } catch (ServiceValidationException e) {
            // Lấy lỗi đầu tiên để trả về message (hoặc gộp tất cả nếu FE cần hiển thị nhiều lỗi)
            String message = e.getErrors().get(0).getMessage();
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        session.removeAttribute("otp_verified");
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}