package vn.edu.fpt.modelview.request.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.model.constant.VenueStatus;

import java.util.List;

@Getter
@Setter
public class CreateVenueDTO {

    @Size(max = 64, message = "Tên Địa điểm không được nhập quá 64 kí tự!")
    @NotBlank(message = "Tên địa điểm không được để trống!")
    private String venueName;

    @Size(max = 255, message = "Địa chỉ chi tiết không được nhập quá 255 kí tự!")
    @NotBlank(message = "Địa chỉ chi tiết không được để trống!")
    private String streetAddress;

    @NotNull(message = "Xã/ Phường không được để trống!")
    private Long ward;

    @NotNull(message = "Tỉnh/ Thành Phố không được để trống!")
    private Long city;

    @NotBlank(message = "Mô tả không được để trống!")
    public String description;

    public String imageUrl;

    @NotNull(message = "Trạng thái không được để trống!")
    private VenueStatus status;

    @NotEmpty(message = "Venue cần có Zone!")
    @Valid
    private List<VenueZoneDTO> zones;
}
