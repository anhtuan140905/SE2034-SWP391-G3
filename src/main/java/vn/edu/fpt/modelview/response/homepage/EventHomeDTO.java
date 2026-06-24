package vn.edu.fpt.modelview.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EventHomeDTO {
    private Long eventId;
    private String title;
    private String thumbnailUrl;
    private String description;
    private String venueName;
    private LocalDateTime startTime;
    private BigDecimal minPrice;
    private String categoryName;
    private String companyName;
}
