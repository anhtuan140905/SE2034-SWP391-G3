package vn.edu.fpt.modelview.response.organizer;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;       // Tổng doanh thu thực tế (PAID)
    private Integer totalTicketsSold;         // Tổng số lượng vé đã bán
    private BigDecimal settledAmount;      // Số tiền kết toán tạm tính
}
