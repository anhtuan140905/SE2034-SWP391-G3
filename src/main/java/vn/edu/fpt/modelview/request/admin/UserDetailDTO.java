package vn.edu.fpt.modelview.request.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailDTO {

    private Long id;
    private String username;
    private String email;
    private String role;

    private List<ActivityDTO> activities;
}
