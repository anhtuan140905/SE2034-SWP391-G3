package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class DashboardOrganizerDTO {

    private Long organizerId;

    private String organizerName;

    private LocalDateTime createdAt;

}
