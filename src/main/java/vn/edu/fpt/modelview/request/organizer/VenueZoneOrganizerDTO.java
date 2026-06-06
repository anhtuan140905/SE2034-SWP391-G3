package vn.edu.fpt.modelview.request.organizer;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VenueZoneOrganizerDTO {
    private Long ZoneID;
    private String zoneName;          // String → dùng @NotBlank
    private Integer rows;             // Integer → dùng @NotNull
    private Integer seatsPerRow;      // Integer → dùng @NotNull
}
