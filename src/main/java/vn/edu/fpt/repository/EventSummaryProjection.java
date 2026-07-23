package vn.edu.fpt.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EventSummaryProjection {
    Long getId();
    String getTitle();
    String getThumbnailUrl();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Double getMinPrice();
    String getCategoryName();
    String getCompany_name();
    String getDescription();
    String getVenueName();
    String getCityName();
    Long getSoldCount();
    Long getParticipantCount();
    BigDecimal getRevenue();
    Long getTotalTickets();
    String getStatus();
    Double getSalesRate();
    String getLastNameOrganizer();
    String getMiddleNameOrganizer();
    String getFirstNameOrganizer();
    String getEmail();

}
