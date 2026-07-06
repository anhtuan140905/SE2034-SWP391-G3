package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.modelview.request.organizer.wardDTO;


@Getter
@Setter
public class addressEditDTO {
    private Long addressId;
    @NotBlank(message = "Địa chỉ chi  tiết không được để trống")
    private String specieladdress;
    @Valid
    private wardEditDTO ward;
}