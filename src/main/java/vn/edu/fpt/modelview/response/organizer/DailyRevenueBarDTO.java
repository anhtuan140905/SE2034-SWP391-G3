package vn.edu.fpt.modelview.response.organizer;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailyRevenueBarDTO {
    private String date;            // Nhãn ngày (dd/MM)
    private BigDecimal revenue;     // Giá trị doanh thu thực tế
    private int heightPercent;      // Tỉ lệ phần trăm chiều cao (0 - 90) để render cột HTML
}
