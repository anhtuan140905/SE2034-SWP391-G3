package vn.edu.fpt.modelview.request.organizer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;

import java.util.List;
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
