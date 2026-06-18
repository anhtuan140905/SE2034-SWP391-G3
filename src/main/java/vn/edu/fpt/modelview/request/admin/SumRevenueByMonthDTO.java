package vn.edu.fpt.modelview.request.admin;

import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.repository.SumRevenueByMonthProjection;

@Getter
@Setter
public class SumRevenueByMonthDTO {
    private Integer month;
    private Long total;

    public SumRevenueByMonthDTO(SumRevenueByMonthProjection projection) {
        this.month = projection.getMonth();
        this.total= projection.getTotal();

    }
}
