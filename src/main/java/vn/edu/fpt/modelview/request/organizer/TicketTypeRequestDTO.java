package vn.edu.fpt.modelview.request.organizer;

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
    @NotBlank(message = "Tên loại vé không được để trống")
    private String typename;
    @NotNull(message = "Giá vé không được để trống")
    @Min(value = 0, message = "Giá vé không được nhỏ hơn 0")
    private BigDecimal price;
    @NotNull(message = "Số lượng vé không được để trống")
    @Min(value = 1, message = "Số lượng vé ít nhất phải là 1")
    private Integer stock;
    private  String description;

}
