package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.modelview.request.organizer.cityDto;

@Getter
@Setter
public class wardEditDTO {
    @NotNull(message = "Phường/Xã phố không được để trống")
    private Long wardId;
    private String name;
    @Valid
    private cityEditDto city;
}