package vn.edu.fpt.modelview.response.homepage;

import lombok.*;
import vn.edu.fpt.repository.EventSummaryProjection;

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
    private Double minPrice;
    private String categoryName;
    private String venueName;
    private String cityName;
    private Long soldCount;

    public EventSummaryDto(EventSummaryProjection projection) {
        this.id = projection.getId();
        this.title = projection.getTitle();
        this.thumbnailUrl = projection.getThumbnailUrl();
        this.startTime = projection.getStartTime();
        this.minPrice = projection.getMinPrice();
        this.categoryName = projection.getCategoryName();
        this.venueName = projection.getVenueName();
        this.cityName = projection.getCityName();
        this.soldCount = projection.getSoldCount();
    }

}