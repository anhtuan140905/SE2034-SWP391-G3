package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

@Data
public class OrganizerManagementStatsDTO {
    private long totalOrganizers;
    private long totalActiveOrganizers;
    private long totalInactiveOrganizers;

}
