package vn.edu.fpt.modelview.request.organizer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
public class EventCardDTO {
    private Long id;
    private String eventName;
    private String thumnail;
    private String eventCatagory;
    private LocalDate date;
    private LocalTime startime;
    private LocalTime endtime;
    private Integer stock;
    private Integer ticketSelled;
    private String statusEvent;
    private String venueName;
    private Integer Percent;
}