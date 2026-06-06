package vn.edu.fpt.repository;

import java.time.LocalDateTime;

public interface EventSummaryProjection {
    Long getId();
    String getTitle();
    String getThumbnailUrl();
    LocalDateTime getStartTime();
    Double getMinPrice();
    String getCategoryName();
    String getVenueName();
    String getCityName();
    Long getSoldCount();
}