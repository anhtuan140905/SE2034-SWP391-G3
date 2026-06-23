package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

import java.time.Instant;

@Data
public class ModeratorOrganizerListDTO {
    private Long organizerId;
    private String fullName;
    private String email;
    private Instant joinedDate;
    private Boolean isActive;

}
