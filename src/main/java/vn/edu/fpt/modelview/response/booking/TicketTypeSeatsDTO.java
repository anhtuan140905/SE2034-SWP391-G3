package vn.edu.fpt.modelview.response.booking;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketTypeSeatsDTO {
    private Long ticketTypeId;
    private String zoneName;
    private BigDecimal price;
    private Integer totalQuantity;
    private Integer soldQuantity;
    private Integer displayOrder;
    private List<SeatStatusDTO> seats;
}
