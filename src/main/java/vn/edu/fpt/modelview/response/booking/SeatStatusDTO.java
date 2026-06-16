package vn.edu.fpt.modelview.response.booking;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatStatusDTO {
    private Long seatId;
    private String rowLabel;
    private Integer seatNumber;
    private String status;
}
