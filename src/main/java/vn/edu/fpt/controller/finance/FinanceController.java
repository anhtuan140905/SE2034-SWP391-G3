package vn.edu.fpt.controller.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.User;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.service.SettlementService;
import vn.edu.fpt.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final SettlementService settlementService;
    private final UserService userService;
    private final OrganizerProfileRepository organizerProfileRepository;

    public FinanceController(SettlementService settlementService,
                             UserService userService,
                             OrganizerProfileRepository organizerProfileRepository) {
        this.settlementService = settlementService;
        this.userService = userService;
        this.organizerProfileRepository = organizerProfileRepository;
    }

    // Default finance officer email used for mock purposes
    private static final String DEFAULT_FINANCE_EMAIL = "sarah.anderson@financeportal.com";

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        User officer = userService.findByUsername(DEFAULT_FINANCE_EMAIL);
        if (officer != null) {
            model.addAttribute("activeUser", officer);
        }
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        SettlementService.FinanceStats stats = settlementService.getStatistics();
        model.addAttribute("stats", stats);
        
        List<Settlement> recentSettlements = settlementService.getAllSettlements();
        // Limit to 5
        model.addAttribute("recentSettlements", recentSettlements.stream().limit(5).collect(Collectors.toList()));
        
        return "finance/DashboardFinance";
    }

    @GetMapping("/events")
    public String getEndedEvents(Model model) {
        List<SettlementService.EndedEventView> events = settlementService.getEndedEvents();
        model.addAttribute("events", events);
        
        SettlementService.FinanceStats stats = settlementService.getStatistics();
        model.addAttribute("stats", stats);
        
        return "finance/ListEndedEvents";
    }

    @GetMapping("/settlement/create")
    public String getCreateSettlementForm(@RequestParam(value = "eventId", required = false) Long eventId, Model model) {
        List<SettlementService.EndedEventView> endedEvents = settlementService.getEndedEvents();
        List<SettlementService.EndedEventView> unsettledEvents = endedEvents.stream()
                .filter(e -> !e.isSettled())
                .collect(Collectors.toList());
        
        model.addAttribute("unsettledEvents", unsettledEvents);
        
        if (eventId != null) {
            Optional<SettlementService.EndedEventView> selectedEventOpt = endedEvents.stream()
                    .filter(e -> e.getEvent().getEventId().equals(eventId))
                    .findFirst();
            selectedEventOpt.ifPresent(selectedEvent -> model.addAttribute("selectedEvent", selectedEvent));
        }
        
        return "finance/CreateSettlement";
    }

    @PostMapping("/settlement/create")
    public String createSettlement(@RequestParam("eventId") Long eventId,
                                   @RequestParam(value = "refundDeduction", required = false) BigDecimal refundDeduction,
                                   @RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "notes", required = false) String notes,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (refundDeduction == null) {
                refundDeduction = BigDecimal.ZERO;
            }
            Settlement settlement = settlementService.createSettlement(eventId, refundDeduction, paymentMethod, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Settlement record created successfully!");
            return "redirect:/finance/settlement/" + settlement.getSettlementId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create settlement: " + e.getMessage());
            return "redirect:/finance/settlement/create?eventId=" + eventId;
        }
    }

    @GetMapping("/settlements")
    public String getSettlements(Model model) {
        List<Settlement> settlements = settlementService.getAllSettlements();
        model.addAttribute("settlements", settlements);
        return "finance/ListSettlement";
    }

    @GetMapping("/settlement/{id}")
    public String getSettlementDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Settlement> settlementOpt = settlementService.getSettlementById(id);
        if (!settlementOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Settlement record not found!");
            return "redirect:/finance/settlements";
        }
        
        Settlement settlement = settlementOpt.get();
        model.addAttribute("settlement", settlement);
        
        Event event = settlement.getEvent();

        // Calculate dynamic tickets count for this settlement details page
        int ticketsSold = 0;
        List<SettlementService.EndedEventView> endedEvents = settlementService.getEndedEvents();
        for (SettlementService.EndedEventView view : endedEvents) {
            if (view.getEvent().getEventId().equals(event.getEventId())) {
                ticketsSold = view.getTicketsSold();
                break;
            }
        }
        model.addAttribute("ticketsSold", ticketsSold);

        // Fetch organizer profile bank account details
        Optional<OrganizerProfile> profileOpt = organizerProfileRepository.findById(event.getOrganizer().getId());
        profileOpt.ifPresent(profile -> model.addAttribute("organizerProfile", profile));
        
        return "finance/ViewSettlementDetails";
    }

    @PostMapping("/settlement/{id}/pay")
    public String paySettlement(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            settlementService.paySettlement(id);
            redirectAttributes.addFlashAttribute("successMessage", "Settlement marked as Paid successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment failed: " + e.getMessage());
        }
        return "redirect:/finance/settlement/" + id;
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        User officer = userService.findByUsername(DEFAULT_FINANCE_EMAIL);
        model.addAttribute("user", officer);
        return "finance/UpdateProfileFinance";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("phone") String phone,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.handleUpdateProfile(DEFAULT_FINANCE_EMAIL, firstName, lastName, phone, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/finance/profile";
    }
}
