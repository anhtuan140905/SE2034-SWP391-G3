package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email không đúng định dạng")
    private String email;

    @NotNull(message = "Role ID không được để trống")
    private Long roleId;

    @NotEmpty(message = "Danh sách Permission không được để trống")
    private List<Long> permissionId;

}
