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
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendeeProfileDTO {
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
    private String email;
    private String password;
    private String confirmPassword;
    @NotBlank(message = "Vui lòng chọn gender")
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String avatar;
    public String city;
    public String ward;
    public String specificAddress;
}
