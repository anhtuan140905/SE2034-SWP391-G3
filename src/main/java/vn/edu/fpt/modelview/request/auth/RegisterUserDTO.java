package vn.edu.fpt.modelview.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "Họ tên không được để trống!")
    private String fullName;
    @NotBlank(message = "Số điện thoại không được để trống!")
    private String phone;
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ")
    private String username;
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
    @NotBlank(message = "Confirm password không được để trống!")
    private String confirmPassword;
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
