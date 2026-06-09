package vn.edu.fpt.modelview.request.admin;

import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.repository.VenueSummaryProjection;

import java.math.BigDecimal;

@Getter
@Setter
public class VenueSummaryDTO {
    private Long eventCount;
    private Long participantCount;
    private BigDecimal revenue;
    private Double usageRate;
    private Integer month;

    public VenueSummaryDTO(VenueSummaryProjection projection){
        this.eventCount = projection.getEventCount();
        this.participantCount = projection.getParticipantCount();
        this.revenue = projection.getRevenue();
        this.usageRate = projection.getUsageRate();
        this.month = projection.getMonth();
    }
}
