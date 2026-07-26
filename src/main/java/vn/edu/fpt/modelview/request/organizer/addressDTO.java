package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;


@Getter
@Setter
public class addressDTO {
    @NotBlank(message = "Địa chỉ chi  tiết không được để trống")
    @Size(max = 255,message = "Tên Địa Điểm Không Được Vượt Quá 255 Ký Tự")
    private String specieladdress;
    @Valid
    private wardDTO ward;
}