package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class seatEditDTO {
    @NotNull(message = "Hàng ghế không được để trống")
    @Min(value = 1, message = "Hàng phải từ 1 đến 26")
    @Max(value = 26, message = "Hàng phải từ 1 đến 26")
    private Integer row;
    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 1, message = "Số ghế phải lớn hơn 0")
    private Integer seatNumber;

}