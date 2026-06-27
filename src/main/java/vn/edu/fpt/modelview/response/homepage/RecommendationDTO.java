package vn.edu.fpt.modelview.response.homepage;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long eventId;
    private String title;
    private String thumbnailUrl;
    private LocalDate date;
    private LocalDateTime startTime;
    private String categoryName;
    private String cityName;
    private BigDecimal minPrice;
    private String reason;
}