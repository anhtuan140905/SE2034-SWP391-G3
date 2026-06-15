package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

@Data
public class DashboardStatsDTO {

    private long activeOrganizers;
    private long activeEvents;
    private long newEventsToday;
    private long inactiveEvents;

}
