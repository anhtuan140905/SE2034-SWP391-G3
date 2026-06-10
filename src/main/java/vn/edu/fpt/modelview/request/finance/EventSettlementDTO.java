package vn.edu.fpt.modelview.request.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class EventSettlementDTO {
    private Long eventId;
    private String title;
    private String organizerName;
    private BigDecimal revenue;
}
