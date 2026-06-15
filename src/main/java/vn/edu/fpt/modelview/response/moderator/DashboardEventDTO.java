package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardEventDTO {

    private Long eventId;
    private String title;
    private LocalDateTime startTime;

}
