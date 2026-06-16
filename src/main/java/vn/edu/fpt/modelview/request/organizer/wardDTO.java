package vn.edu.fpt.modelview.request.organizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class wardDTO {
    @NotNull(message = "Phường/Xã phố không được để trống")
    private Long wardId;
    private String name;
    @Valid
    private cityDto city;
}
