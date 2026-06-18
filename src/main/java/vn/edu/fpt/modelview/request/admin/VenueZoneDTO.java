package vn.edu.fpt.modelview.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VenueZoneDTO {

    @Size(max = 64, message = "Tên Khu Vực không được nhập quá 64 kí tự!")
    @NotBlank(message = "Tên Khu Vực không được để trống")
    private String zoneName;  // String → dùng @NotBlank


    @NotNull(message = "Số hàng không được để trống!")
    @Min(value = 1, message = "Số hàng phải lớn hơn 0!")
    private Integer rows;             // Integer → dùng @NotNull

    
    @NotNull(message = "Số ghế/hàng không được để trống!")
    @Min(value = 1, message = "Số ghế/hàng phải lớn hơn 0!")
    private Integer seatsPerRow;      // Integer → dùng @NotNull
}
