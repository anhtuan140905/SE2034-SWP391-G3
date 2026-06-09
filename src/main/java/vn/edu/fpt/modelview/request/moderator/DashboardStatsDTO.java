package vn.edu.fpt.modelview.request.moderator;

import lombok.Data;

@Data
public class DashboardStatsDTO {

    private long pendingEvents;
    private long activeEvents;
    private long approvedToday;
    private long rejectedEvents;

}
