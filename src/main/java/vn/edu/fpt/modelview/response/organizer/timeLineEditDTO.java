package vn.edu.fpt.modelview.response.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class timeLineEditDTO {
    private Long timeLineId;
    private LocalTime time;
    private  String active;
}