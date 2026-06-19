package vn.edu.fpt.modelview.request.moderator;

import lombok.Data;

@Data
public class DeactivateEventRequestDTO {
    private String eventId;
    private String reason;
}
