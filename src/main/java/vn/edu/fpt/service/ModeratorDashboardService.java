package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.moderator.DashboardEventDTO;
import vn.edu.fpt.modelview.response.moderator.DashboardStatsDTO;

import java.util.List;

public interface ModeratorDashboardService {

    DashboardStatsDTO getDashboardStats();
    List<DashboardEventDTO>  getRecentEvents();
    List<DashboardEventDTO>  getTodayEvents();

}
