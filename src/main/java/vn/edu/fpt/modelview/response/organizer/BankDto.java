package vn.edu.fpt.modelview.response.organizer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankDto {
    @NotNull(message = "Không được để trống Ngân Hàng")
    private Long bankId;
    private String bankName;
}
