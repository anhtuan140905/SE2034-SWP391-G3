package vn.edu.fpt.modelview.response.homepage;

import lombok.*;
import vn.edu.fpt.repository.EventSummaryProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDto {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double minPrice;
    private String categoryName;
    private String venueName;
    private String cityName;
    private Long soldCount;
    private Long participantCount;
    private BigDecimal revenue;
    private Long totalTickets;
    private String status;
    private Double salesRate;


    public EventSummaryDto(EventSummaryProjection projection) {
        this.id = projection.getId();
        this.title = projection.getTitle();
        this.thumbnailUrl = projection.getThumbnailUrl();
        this.startTime = projection.getStartTime();
        this.endTime = projection.getEndTime();
        this.minPrice = projection.getMinPrice();
        this.categoryName = projection.getCategoryName();
        this.venueName = projection.getVenueName();
        this.cityName = projection.getCityName();
        this.soldCount = projection.getSoldCount();
        this.participantCount = projection.getParticipantCount();
        this.revenue = projection.getRevenue();
        this.totalTickets = projection.getTotalTickets();
        this.status = projection.getStatus();
        this.salesRate = projection.getSalesRate();
    }

}
