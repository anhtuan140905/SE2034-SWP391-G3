package vn.edu.fpt.modelview.response.homepage;

import lombok.*;
import vn.edu.fpt.model.constant.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSearchResultDTO {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private String description;

    private String categoryName;

    private String venueName;
    private String city;

    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String organizerName;
    private Double minPrice;
    private Long soldTickets;

    private Long totalCapacity;
    private EventStatus status;
}
