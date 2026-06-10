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

    
}