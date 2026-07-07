package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.organizer.*;
import java.util.List;

public interface OrganizerDashboardService {
    DashboardStatsDTO getDashboardStats(Long eventId);
    List<DailyRevenueBarDTO> getDailyRevenueChartData(Long eventId);
    List<TicketTypeStatsDTO> getTicketTypeStats(Long eventId);
}
