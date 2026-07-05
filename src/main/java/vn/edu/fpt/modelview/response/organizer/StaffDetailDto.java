package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class StaffDetailDto {
    private Long staffId;
    private String fullName;
    private String email;
    private Long roleId;
    @NotEmpty(message = "Không Được để Trông Quyền Cụ Thể")
    private List<Long> permission;
}
