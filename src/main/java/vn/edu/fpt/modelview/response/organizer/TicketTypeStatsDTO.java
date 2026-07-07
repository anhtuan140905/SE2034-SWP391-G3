package vn.edu.fpt.modelview.response.organizer;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TicketTypeStatsDTO {
    private String zoneName;
    private BigDecimal price;
    private Integer soldQuantity;
    private Integer totalStock;
    private Integer percentSold;
}
