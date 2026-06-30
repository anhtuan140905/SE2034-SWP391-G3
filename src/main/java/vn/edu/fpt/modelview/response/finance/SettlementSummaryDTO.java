package vn.edu.fpt.modelview.response.finance;

import vn.edu.fpt.repository.SettlementSummaryProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SettlementSummaryDTO {
    private Long settlementId;
    private Long eventId;
    private String eventName;
    private String lastNameOrganizer;
    private String middleNameOrganizer;
    private String firstNameOrganizer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String categoryName;
    private String venueName;
    private String cityName;
    private Long soldTicket;
    private Long participantCount;
    private BigDecimal revenue;
    private Long totalTickets;
    private String status;

    public SettlementSummaryDTO(SettlementSummaryProjection projection) {
        this.settlementId = projection.getSettlementId();
        this.eventId = projection.getEventId();
        this.eventName = projection.getEventName();
        this.lastNameOrganizer = projection.getLastNameOrganizer();
        this.middleNameOrganizer = projection.getMiddleNameOrganizer();
        this.firstNameOrganizer = projection.getFirstNameOrganizer();
        this.startTime = projection.getStartTime();
        this.endTime = projection.getEndTime();
        this.categoryName = projection.getCategoryName();
        this.venueName = projection.getVenueName();
        this.cityName = projection.getCityName();
        this.soldTicket = projection.getSoldTicket();
        this.participantCount = projection.getParticipantCount();
        this.revenue = projection.getRevenue();
        this.totalTickets = projection.getTotalTickets();
        this.status= projection.getStatus();
    }
}
