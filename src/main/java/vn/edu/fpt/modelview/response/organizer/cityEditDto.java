package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class cityEditDto {

    @NotNull(message = "Tỉnh/Thành phố không được để trống")
    private Long id;
    private String name;
}