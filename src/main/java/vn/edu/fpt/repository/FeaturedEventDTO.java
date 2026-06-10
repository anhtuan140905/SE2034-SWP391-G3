package vn.edu.fpt.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FeaturedEventDTO {
    Long getEvent_id();
    String getTitle();
    String getThumbnail_url();
    LocalDateTime getStartTime();
    String getVenueName();
    String getSpecificAddress();
    String getName();
    BigDecimal getMinPrice();
    String getCategoryName();
    String getOrganizerName();
    Integer getSoldCount();
}