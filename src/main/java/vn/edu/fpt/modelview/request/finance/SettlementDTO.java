package vn.edu.fpt.modelview.request.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.constant.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDTO {

    private Long eventId;
    private String organizerName;
    private BigDecimal grossRevenue;
    private BigDecimal platformFee;
    private BigDecimal payoutAmount;
    private SettlementStatus status;
    private Instant paidAt;
}