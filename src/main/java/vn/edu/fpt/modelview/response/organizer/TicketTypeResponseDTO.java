package vn.edu.fpt.modelview.response.organizer;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.modelview.request.organizer.seatDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class TicketTypeResponseDTO {
    private Long ticketTypeId;
    @NotNull(message = "Không được để trống mức độ ưu tiên")
    private Integer displayOrder;
    @NotBlank(message = "Không được để trống tên hạng vé")
    @Size(max = 100, message = "Tên hạng vé không được vượt quá 100 ký tự")
    private String zoneName;
    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "0.0", message = "Giá vé không được âm")
    private BigDecimal price;
    @NotNull(message = "Số lượng vé không được để trống")
    @Positive(message = "Số lượng vé phải lớn hơn 0")
    private Long stock;
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;
    @Valid
    private seatEditDTO seat;
}