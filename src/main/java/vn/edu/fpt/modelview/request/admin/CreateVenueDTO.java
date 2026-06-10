package vn.edu.fpt.modelview.request.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.model.constant.VenueStatus;

import java.util.List;

@Getter
@Setter
public class CreateVenueDTO {

    @NotBlank(message = "Tên venue không được để trống!")
    private String venueName;

    @NotBlank(message = "StreetAddress không được để trống")
    private String streetAddress;

    @NotNull(message = "Ward không được để trống")
    private Long ward;

    @NotNull(message = "City không được để trống")
    private Long city;

    @NotBlank(message = "Description không được để trống")
    public String description;

    public String imageUrl;

    private VenueStatus status;

    @NotEmpty(message = "Venue cần có Zone")
    @Valid
    private List<VenueZoneDTO> zones;
}