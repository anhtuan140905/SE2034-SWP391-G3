package vn.edu.fpt.modelview.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrgDTO {
    @NotBlank(message = "First name không được để trống!")
    private String firstName;
    @NotBlank(message = "Middle name không được để trống!")
    private String middleName;
    @NotBlank(message = "Last name không được để trống!")
    private String lastName;
    @NotBlank(message = "Số điện thoại không được để trống!")
    private String phone;
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ")
    private String username;
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
    @NotBlank(message = "Confirm password không được để trống!")
    private String confirmPassword;
    @NotBlank(message = "Vui lòng chọn gender")
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;
    @NotBlank(message = "Tax code không được để trống")
    private String taxCode;
    @NotBlank(message = "Bank account không được để trống")
    private String bankAccount;
}
