package vn.edu.fpt.modelview.request.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityDTO {
    private String action;
    private String description;
    private LocalDateTime time;
    private String referenceId;
    private String userEmail;
    private String userName;
    private String iconType;
    private String status;
}