package vn.edu.fpt.modelview.response.booking;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEmailDTO {
    private Long orderId;
    private String userFullName;
    private String userEmail;
    private BigDecimal totalAmount;
    List<TicketEmailDTO> tickets;
}
