package vn.edu.fpt.modelview.response.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeDTO {
    private String zoneName;
    private BigDecimal price;
    private Integer Stock;
    private Integer soldQuantity;
}
