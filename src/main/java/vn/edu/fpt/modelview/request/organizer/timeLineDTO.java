package vn.edu.fpt.modelview.request.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class timeLineDTO {
    private LocalTime time;
    private  String active;
}