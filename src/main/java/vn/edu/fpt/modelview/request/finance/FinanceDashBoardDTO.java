package vn.edu.fpt.modelview.request.finance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceDashBoardDTO {
    private Double totalRevenue;
    private Long pendingCount;
    private Double paidAmount;
    private Long completedEvents;

}
