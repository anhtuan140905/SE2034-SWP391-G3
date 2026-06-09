package vn.edu.fpt.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FeaturedEventDTO {
    Long getEvent_id();
    String getTitle();
    String getThumbnail_url();
    LocalDateTime getStart_time();
    BigDecimal getMin_price();
    String getOrganizer_name();
    Integer getSold_count();
}
