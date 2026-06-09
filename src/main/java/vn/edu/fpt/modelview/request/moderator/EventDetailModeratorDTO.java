package vn.edu.fpt.modelview.request.moderator;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDetailModeratorDTO {

    private Long id;
    private String title;
    private String status;
    private String coverImageUrl;
    private Long categoryId;
    private String category;
    private String description;

    private LocalDateTime  startTime ;
    private String duration;

    private String venueName;
    private String venueAddress;
    private Integer capacity;
    private Integer totalTickets;

    private Long organizerId;
    private String organizerName;
    private String organizerAvatarUrl;

    private String rejectReason;

}
