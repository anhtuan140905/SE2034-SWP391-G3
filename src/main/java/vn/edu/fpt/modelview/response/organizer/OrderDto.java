package vn.edu.fpt.modelview.response.organizer;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class OrderDto {
    private String eventTitle;
    private Long orderId;
    private String fullName;
    private String Phone;
    private String createAt;
    private String quantityTicket;
    private BigDecimal totalAmount;
    private String Status;
}
