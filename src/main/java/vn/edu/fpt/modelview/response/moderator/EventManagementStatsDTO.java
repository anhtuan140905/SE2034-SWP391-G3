package vn.edu.fpt.modelview.response.moderator;

import lombok.Data;

@Data
public class EventManagementStatsDTO {

    private long totalEvents;
    private long eventsToday;
    private long inactiveEvents;
    private long endedThisMonth;

}
