package vn.edu.fpt.modelview.response.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.TicketType;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.organizer.TicketTypeRequestDTO;
import vn.edu.fpt.modelview.request.organizer.timeLineDTO;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailDTO {
    private String Banner;
    private String eventName;
    private String category;
    private String VenueName;
    private String date;
    private String status;
    private String description;
    private List<String> urlImage;
    private String city;
    private List<timeLineDTO> timelines;
    private List<TicketTypeDTO> ticketType;
    private String startTime;
    private String endTime;
    private String ward;
    private String specificAddress;


}
