package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;
import vn.edu.fpt.model.constant.EventStatus;

import java.time.LocalDateTime;

@Data
public class ModeratorEventListDTO {

    private Long eventId;
    private String title;
    private String organizerName;
    private LocalDateTime startTime;
    private EventStatus status;

}
