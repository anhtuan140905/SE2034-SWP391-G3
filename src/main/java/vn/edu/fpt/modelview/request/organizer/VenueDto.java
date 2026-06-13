package vn.edu.fpt.modelview.request.organizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VenueDto {
    private Long venueID;
    private String venueName;
    private AddressDto address;
    private Integer capacity;
    private String description;
    private String imageUrl;
    private VenueZoneDTO Zone;

}
