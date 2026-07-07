package vn.edu.fpt.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public interface SettlementSummaryProjection {
    Long getSettlementId();
    Long getEventId();
    String getEventName();
    String getLastNameOrganizer();
    String getMiddleNameOrganizer();
    String getFirstNameOrganizer();
    BigDecimal getPayoutAmount();
    BigDecimal getPlatformFee();
    LocalDateTime getCreateAt();
    String getStatus();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    String getCategoryName();
    String getVenueName();
    String getCityName();
    Long getSoldTicket();
    Long getParticipantCount();
    BigDecimal getRevenue();
    Long getTotalTickets();
    String getLastNameFinance();
    String getMiddleNameFinance();
    String getFirstNameFinance();
    LocalDateTime getUpdateAt();
    LocalDateTime getPaidAt();
    String getCreatedBy();
    Integer getMonth();


}
