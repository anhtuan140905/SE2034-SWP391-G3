package vn.edu.fpt.modelview.response.organizer;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class TicketTypeCheckinDto {
    private String typeName;
    private BigDecimal price;
    private Long checked;
    private Integer soldTicket;
    private Integer percent;
}
