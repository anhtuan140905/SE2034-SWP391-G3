package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class seatDTO {
    @NotNull(message = "Hàng ghế không được để trống")
    @Min(value = 1, message = "Hàng phải từ 1 đến 26")
    @Max(value = 26, message = "Hàng phải từ 1 đến 26")
    private Integer row;
    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 1, message = "Số ghế phải lớn hơn 0")
    private Integer seatNumber;

}