package vn.edu.fpt.modelview.request.admin;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String username;
    private LocalDate dob;
    private String city;
    private String ward;
    private String specificAddress;
    private Long userRoleId;
    private String roleName;
    private Boolean isActive;
}
