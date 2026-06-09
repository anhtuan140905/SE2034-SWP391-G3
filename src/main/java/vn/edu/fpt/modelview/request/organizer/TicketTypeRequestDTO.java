package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketTypeRequestDTO {
    private String typeName;
    private Long zoneID;
    private BigDecimal price;
    private Long Stock;
    private String description;

}
