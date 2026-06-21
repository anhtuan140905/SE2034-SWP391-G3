package vn.edu.fpt.modelview.request.moderator;

import lombok.Data;

@Data
public class DeactivateEventDTO {
    private Long eventId;
    private String reason;
}
