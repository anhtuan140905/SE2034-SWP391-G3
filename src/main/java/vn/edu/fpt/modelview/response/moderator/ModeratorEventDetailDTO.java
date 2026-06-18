package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;
import vn.edu.fpt.model.constant.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ModeratorEventDetailDTO {

    private Long eventId;
    private String title;
    private EventStatus status;
    private String thumbnailUrl;
    private String categoryName;
    private String description;

    private LocalDate date;
    private LocalDateTime startTime ;
    private LocalDateTime endTime ;

    private String venueName;
    private String venueAddress;
    private String cityName;

    private Long organizerId;
    private String organizerName;
    private String organizerEmail;
    private String organizerAvatarUrl;

    private String deactivateReason;

}
