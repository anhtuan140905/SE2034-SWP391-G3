package vn.edu.fpt.modelview.request.organizer;

import jakarta.persistence.Column;
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
public class cityDto {

    @NotNull(message = "Tỉnh/Thành phố không được để trống")
    private Long id;
    private String name;
}
