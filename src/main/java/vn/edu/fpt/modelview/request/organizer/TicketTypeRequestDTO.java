package vn.edu.fpt.modelview.request.organizer;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Không được để trống mức độ ưu tiên")
    private Integer DisplayOrder;
    @NotBlank(message = "Không được để trống tên hạng vé")
    @Size(max = 100, message = "Tên hạng vé không được vượt quá 100 ký tự")
    private String zoneName;
    @NotNull(message = "Giá vé không được để trống")
    private BigDecimal price;
    @NotNull(message = "Số lượng vé không được để trống")
    @Positive(message = "Số lượng vé phải lớn hơn 0")
    private Long Stock;
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;
    @Valid
    private seatDTO seat;
}
