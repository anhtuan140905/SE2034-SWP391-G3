package vn.edu.fpt.modelview.request.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountEventByMonthDTO {
    private Integer month;
    private Long total;

    public CountEventByMonthDTO(Integer month, Long total) {
        this.month = month;
        this.total = total;
    }
}
