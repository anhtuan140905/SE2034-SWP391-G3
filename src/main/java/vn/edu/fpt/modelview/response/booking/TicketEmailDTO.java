package vn.edu.fpt.modelview.response.booking;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketEmailDTO {
    private String eventTitle;
    private String seatCode;
    private String ticketCode;
    private String qrCode;
}
