package vn.edu.fpt.controller.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.service.FinanceService;

import java.util.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats",             financeService.getDashboardStats());
        model.addAttribute("recentSettlements", financeService.getRecentSettlements());
        model.addAttribute("events",            financeService.getAllEvents());
        return "finance/DashboardFinance";
    }
    @GetMapping("/events")
    public String events(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "")   String keyword,
            Model model) {


        List<Event> allEnded = financeService.getEndedEvents();


        List<Event> events;
        switch (status.toUpperCase()) {
            case "UNSETTLED":
                events = allEnded.stream()
                        .filter(e -> !financeService.getSettledEventIds(allEnded).contains(e.getEventId()))
                        .toList();
                break;
            case "SETTLED":
                events = allEnded.stream()
                        .filter(e -> financeService.getSettledEventIds(allEnded).contains(e.getEventId()))
                        .toList();
                break;
            default:
                events = allEnded;
                break;
        }


        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(kw)
                            || (e.getOrganizer().getFirstName() + " " + e.getOrganizer().getLastName())
                            .toLowerCase().contains(kw))
                    .toList();
        }


        java.util.Set<Long> settledIds = financeService.getSettledEventIds(allEnded);

        model.addAttribute("events",             events);
        model.addAttribute("settledEventIds",    settledIds);
        model.addAttribute("selectedStatus",     status);
        model.addAttribute("keyword",            keyword);
        model.addAttribute("totalEvents",        events.size());
        model.addAttribute("awaitingSettlement", financeService.countAwaitingSettlement(allEnded));
        model.addAttribute("totalRevenue",       financeService.getTotalRevenue());
        return "finance/ListEndedEvents";
    }
    @GetMapping("/settlement/create")
    public String createSettlementPage(Model model) {
        model.addAttribute("unsettledEvents", financeService.getUnsettledEvents());
        return "finance/CreateSettlement";
    }

    @PostMapping("/settlement/create")
    public String createSettlement(
            @RequestParam Long eventId,
            @RequestParam(defaultValue = "0") Double refundDeduction,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes,
            RedirectAttributes ra) {
        try {
            financeService.createSettlement(eventId, refundDeduction, paymentMethod, notes);
            ra.addFlashAttribute("successMessage", "Settlement created successfully!");
            return "redirect:/finance/settlements";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/finance/settlement/create";
        }
    }
    @GetMapping("/settlements")
    public String settlements(
            @RequestParam(defaultValue = "ALL") String status,
            Model model) {
        model.addAttribute("settlements",    financeService.getSettlementsByStatus(status));
        model.addAttribute("selectedStatus", status);
        return "finance/ListSettlement";
    }



}