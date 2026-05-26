package vn.edu.fpt.modelview.request.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.model.VenueZone;

import java.util.List;

@Getter
@Setter
public class CreateVenueDTO {
    @NotBlank(message = "Tên venue không được để trống!")
    private String venueName;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    private String address;

    @NotBlank(message = "Thành phố không được để trống")
    public String city;

    public String description;

    public String imageUrl;

    @NotEmpty(message = "Venue cần có ZOne")
    @Valid
    private List<VenueZoneDTO> zones;
}
