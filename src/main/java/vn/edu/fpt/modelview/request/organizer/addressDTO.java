package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class addressDTO {
    @NotBlank(message = "Địa chỉ chi  tiết không được để trống")
    private String specieladdress;
    @Valid
    private wardDTO ward;
}