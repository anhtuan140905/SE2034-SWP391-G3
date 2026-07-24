package vn.edu.fpt.modelview.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.fpt.model.constant.Gender;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendeeProfileDTO {
    @NotBlank(message = "Tên họ không được để trống!")
    private String firstName;

    @NotBlank(message = "Tên đệm name không được để trống!")
    private String middleName;

    @NotBlank(message = "tên không được để trống!")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống!")
    private String phone;

    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String oldPassword;

    private String password;

    private String confirmPassword;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String avatar;

    @NotBlank(message = "Vui lòng chọn Tỉnh/Thành phố")
    public String city;

    @NotBlank(message = "Vui lòng chọn Phường/Xã")
    public String ward;

    public String specificAddress;
}