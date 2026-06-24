package vn.edu.fpt.modelview.request.moderator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrganizerRequest {

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không đúng định dạng.")
    private String email;

    @NotBlank(message = "Mật khầu không được để trống.")
    @Size(min = 8, message = "Mật khẩu phải lớn hơn 8 ký tự.")
    private String password;

    @NotBlank(message = "Họ không được để trống.")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Tên không được để trống.")
    private String lastName;
}
    