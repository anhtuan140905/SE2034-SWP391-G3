package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class seatDTO {
    @NotBlank(message = "Hàng ghế không được để trống")
    private String row;
    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 1, message = "Số ghế phải lớn hơn 0")
    private Integer seatNumber;

}