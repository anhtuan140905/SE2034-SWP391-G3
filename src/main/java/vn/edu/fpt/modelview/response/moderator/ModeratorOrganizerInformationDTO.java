package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

import java.time.Instant;

@Data
public class ModeratorOrganizerInformationDTO {

    private Long organizerId;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;

    private String homeAddress;
    private String wardName;
    private String cityName;

    private String companyName;
    private String taxCode;
    private String businessType;

    private Instant joinedDate;
    private long totalEventsOrganized;
    private long totalInactiveEvents;

}

