package vn.edu.fpt.modelview.request.admin;

import jakarta.persistence.Column;
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

    @NotBlank(message = "StreetAddress không được để trống")
    private String streetAddress;
    @NotBlank(message = "Ward không được để trống")
    private String ward;
    @NotBlank(message = "City không được để trống")
    private String city;
    @NotBlank(message = "Description không dược để trống")
    public String description;
    public String imageUrl;

    @NotEmpty(message = "Venue cần có ZOne")
    @Valid
    private List<VenueZoneDTO> zones;
}
