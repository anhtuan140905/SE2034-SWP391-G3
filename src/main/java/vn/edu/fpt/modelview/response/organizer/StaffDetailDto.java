package vn.edu.fpt.modelview.response.organizer;

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
    private List<Long> permission;
}
