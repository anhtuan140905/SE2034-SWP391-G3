package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
public class OrganizerDTO {
    private Long OrganizerID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String gender;
    private LocalDate dob;
    private String avatar;
    public String city;
    public String ward;
    public String specificAddress;
}
