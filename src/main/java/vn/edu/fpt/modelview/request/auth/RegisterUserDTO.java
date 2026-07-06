package vn.edu.fpt.modelview.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.fpt.model.constant.Gender;

import java.time.LocalDate;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "Họ không được để trống!")
    @Size(max = 10, message = "Họ tối đa 20 ký tự")
    private String firstName;
    @NotBlank(message = "Tên đệm không được để trống!")
    @Size(max = 15, message = "Tên đệm tối đa 20 ký tự")
    private String middleName;
    @NotBlank(message = "Tên không được để trống!")
    @Size(max = 10, message = "Tên tối đa 20 ký tự")
    private String lastName;
    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ")
    private String username;
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @NotBlank(message = "Confirm password không được để trống!")
    private String confirmPassword;
    @NotNull(message = "Vui lòng chọn giới tính")
    private Gender gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}