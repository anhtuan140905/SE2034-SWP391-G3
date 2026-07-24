package vn.edu.fpt.modelview.response.finance;

import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.repository.SettlementSummaryProjection;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
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
    private BigDecimal platformFee;
    private BigDecimal payoutAmount;
    private Long totalTickets;
    private String status;
    private LocalDateTime createAt;
    private String lastNameFinance;
    private String middleNameFinance;
    private String firstNameFinance;
    private LocalDateTime updateAt;
    private LocalDateTime paidAt;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankBranch;
    private String bankName;
    private String timeDisplay;
    private String createdBy;
    private Integer month;



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
        this.payoutAmount = projection.getPayoutAmount();
        this.createAt = projection.getCreateAt();
        this.firstNameFinance = projection.getFirstNameFinance();
        this.middleNameFinance = projection.getMiddleNameFinance();
        this.lastNameFinance = projection.getLastNameFinance();
        this.platformFee = projection.getPlatformFee();
        this.updateAt = projection.getUpdateAt();
        this.paidAt = projection.getPaidAt();
        this.bankAccountName = projection.getBankAccountName();
        this.bankAccountNumber = projection.getBankAccountNumber();
        this.bankBranch = projection.getBankBranch();
        this.bankName = projection.getBankName();
        this.createdBy = projection.getCreatedBy();
        this.month = projection.getMonth();
    }

    public void calculateTimeDisplay() {

        if ("PENDING".equals(status)) {

            LocalDateTime deadline = createAt.plusHours(72);

            long hours = Duration.between(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), deadline).toHours();

            if (hours < 0) {
                timeDisplay = "Quá hạn " + (-hours) + " giờ";
            } else {
                timeDisplay = "Còn " + hours + " giờ";
            }

        } else if ("COMPLETED".equals(status)) {

            if (paidAt != null) {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

                timeDisplay = "Đã trả lúc " + paidAt.format(formatter);
            } else {
                timeDisplay = "Đã thanh toán";
            }
        }
    }
}
