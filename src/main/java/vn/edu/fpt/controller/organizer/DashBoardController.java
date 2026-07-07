package vn.edu.fpt.controller.organizer;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.OrganizerDashboardService;
import vn.edu.fpt.modelview.response.organizer.*;
import java.util.List;

@Controller
@RequestMapping("/organizer")
@AllArgsConstructor
public class DashBoardController {

    private final OrganizerDashboardService dashboardService;

    @GetMapping("/event/{id}/dashboard")
    public String dashboard(@PathVariable("id") Long eventId, Model model) {
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("eventId", eventId);

        // 1. Số liệu tổng hợp
        DashboardStatsDTO stats = dashboardService.getDashboardStats(eventId);
        model.addAttribute("stats", stats);

        // 2. Dữ liệu biểu đồ cột
        List<DailyRevenueBarDTO> chartDataList = dashboardService.getDailyRevenueChartData(eventId);
        model.addAttribute("chartDataList", chartDataList);

        // 3. Danh sách hạng vé
        List<TicketTypeStatsDTO> ticketStats = dashboardService.getTicketTypeStats(eventId);
        model.addAttribute("ticketStats", ticketStats);

        return "organizer/DashboardOrganizer";
    }

}
