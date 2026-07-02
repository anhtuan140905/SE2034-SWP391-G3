package vn.edu.fpt.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SettlementSummaryProjection {
    Long getSettlementId();
    Long getEventId();
    String getEventName();
    String getLastNameOrganizer();
    String getMiddleNameOrganizer();
    String getFirstNameOrganizer();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    String getCategoryName();
    String getVenueName();
    String getCityName();
    Long getSoldTicket();
    Long getParticipantCount();
    BigDecimal getRevenue();
    Long getTotalTickets();
    String getStatus();
}
