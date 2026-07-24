package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message ="Không được để trông thời gian")
    private LocalTime time;
    @NotNull(message = "không được để trống hoạt động")
    private  String active;
}