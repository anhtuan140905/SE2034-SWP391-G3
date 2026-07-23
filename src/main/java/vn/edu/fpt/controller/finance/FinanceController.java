package vn.edu.fpt.controller.finance;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.modelview.response.finance.SettlementSummaryDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.repository.SettlementAgingProjection;
import vn.edu.fpt.repository.SettlementSummaryProjection;
import vn.edu.fpt.service.*;
import vn.edu.fpt.security.CustomOAuth2User;
import vn.edu.fpt.security.CustomUserDetails;
import vn.edu.fpt.service.impl.UserServiceImpl;


import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/finance")

public class FinanceController {
    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final SettlementService settlementService;
    private final EventCategoryService eventCategoryService;
    private final UserServiceImpl userServiceImpl;


    public FinanceController(UserService userService,
                             EventService eventService,
                             TicketService ticketService,
                             SettlementService settlementService,
                             EventCategoryService eventCategoryService,
                             UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.settlementService = settlementService;
        this.eventCategoryService = eventCategoryService;
        this.userServiceImpl = userServiceImpl;

    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);


        Long totalRevenue = eventService.sumTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue);

        Long pendingPayoutAmount = settlementService.sumPendingPayoutAmount();
        model.addAttribute("pendingPayoutAmount", pendingPayoutAmount);

        long nearDueCount = settlementService.countNearDuePendingSettlements();
        model.addAttribute("nearDueCount", nearDueCount);

        Long totalPaidAmount = settlementService.sumPayoutAmount();
        model.addAttribute("totalPaidAmount", totalPaidAmount);

        long unsettledEventCount = settlementService.countUnsettledEvents();
        model.addAttribute("unsettledEventCount", unsettledEventCount);

        List<SettlementSummaryProjection> platformFeeByMonth = settlementService.platformFeeByMonth();
        model.addAttribute("platformFeeByMonth", platformFeeByMonth);

        SettlementAgingProjection settlementAging = settlementService.getSettlementAging();
        model.addAttribute("settlementAging", settlementAging);

        return "finance/DashboardFinance";
    }

    @PostMapping("/createSettlement")
    public String createSettlementPage(@ModelAttribute SettlementDTO dto,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/finance/createSettlement?eventId=" + dto.getEventId();
        }

        try {
            settlementService.createSettlement(dto);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/finance/createSettlement?eventId=" + dto.getEventId();
        }

        return "redirect:/finance/listEndedEvents";
    }


    @GetMapping("/createSettlement")
    public String getCreateSettlementPage(Model model,
                                          @RequestParam(required = false, defaultValue = "all") String tab,
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                          @RequestParam(required = false) Long eventId) {

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        List<SettlementSummaryProjection> listEndedEvents = eventService.findEventsWithSettlementStatus(tab);
        model.addAttribute("listEndedEvents", listEndedEvents);

        SettlementSummaryProjection eventDetail = settlementService.findEventDetailById(eventId);
        model.addAttribute("eventDetail", eventDetail);

        SettlementDTO dto = new SettlementDTO();
        dto.setEventId(eventId);

        model.addAttribute("settlementDTO", dto);


        return "finance/CreateSettlement";
    }

    @GetMapping("/listSettlement")
    public String listSettlementPage(Model model,
                                     @RequestParam(defaultValue = "all") String tab,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                     @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        try {
            User currentUser = (userDetails != null)
                    ? userService.findByUsername(userDetails.getUsername())
                    : userService.findByUsername(oAuth2Users.getName());
            model.addAttribute("currentUser", currentUser);

            long countAllSettlement = settlementService.countAllSettlement();
            model.addAttribute("countAllSettlement", countAllSettlement);

            long countPendingSettlement = settlementService.countPendingSettlement();
            model.addAttribute("countPendingSettlement", countPendingSettlement);

            long countCompletedSettlement = settlementService.countCompletedSettlement();
            model.addAttribute("countCompletedSettlement", countCompletedSettlement);

            Long totalPaidAmount = settlementService.sumPayoutAmount();
            model.addAttribute("totalPaidAmount", totalPaidAmount);

            List<SettlementSummaryDTO> listSettlements;
            if (keyword != null && !keyword.trim().isEmpty()) {
                listSettlements = settlementService.searchSettlement(keyword);
                if (listSettlements.isEmpty()) {
                    model.addAttribute("notFoundMessage",
                            "Không tìm thấy kết quả nào khớp với từ khóa " + keyword.trim() + ".");
                }
            } else {
                listSettlements = settlementService.listSettlement(tab);
            }

            model.addAttribute("listSettlements", listSettlements);
            model.addAttribute("tab", tab);
            model.addAttribute("keyword", keyword);

            return "finance/ListSettlement";

        } catch (Exception ex) {

            return "redirect:/listSettlement";
        }
    }

    @GetMapping("/viewSettlementDetails")
    public String viewSettlementDetailsPage(Model model,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                            @RequestParam Long settlementId) {

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        EventSummaryProjection eventDetail = eventService.getEventDetail(settlementId);
        model.addAttribute("eventDetail", eventDetail);

        SettlementSummaryProjection settlementDetail = settlementService.getSettlementDetail(settlementId);
        model.addAttribute("settlementDetail", settlementDetail);

        return "finance/ViewSettlementDetails";
    }

    @PostMapping("/settlements/{settlementId}/complete")
    public String completedSettlement(@PathVariable Long settlementId,
                                      RedirectAttributes redirectAttributes) {
        try {
            settlementService.markAsCompleted(settlementId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã đánh dấu quyết toán là Đã Thanh Toán.");
        } catch (EntityNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        }
        return "redirect:/finance/viewSettlementDetails?settlementId=" + settlementId;
    }

    @GetMapping("/listEndedEvents")
    public String listEndedEventsPage(Model model,
                                      @RequestParam(defaultValue = "all") String tab,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        User currentUser = (userDetails != null)
                ? userService.findByUsername(userDetails.getUsername())
                : userService.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);


        List<SettlementSummaryProjection> listEndedEvents;

        try{
            if (keyword != null && !keyword.trim().isEmpty()) {
                listEndedEvents = eventService.searchEndedEvents(keyword);

                if (listEndedEvents.isEmpty()) {
                    model.addAttribute("notFoundMessage",
                            "Không tìm thấy kết quả nào khớp với từ khóa " + keyword.trim() + ".");
                }
            } else {
                listEndedEvents = eventService.findEventsWithSettlementStatus(tab);
            }
        }
        catch (IllegalArgumentException ex){
            model.addAttribute("error", ex.getMessage());
            listEndedEvents = Collections.emptyList();
        }

        model.addAttribute("listEndedEvents", listEndedEvents);
        model.addAttribute("tab", tab);
        model.addAttribute("keyword", keyword);


        long countEndEvent = eventService.countEndedEvent();
        model.addAttribute("countEndEvent", countEndEvent);

        long unsettledEventCount = eventService.countUnsettledEvents();
        model.addAttribute("unsettledEventCount", unsettledEventCount);

        long settledEventCount = settlementService.countPendingSettlement();
        model.addAttribute("settledEventCount", settledEventCount);


        return "finance/ListEndedEvents";
    }

    @GetMapping("/")
    public String homepage(
            Model model){
        long hostedEvents = this.eventService.countHostedEvents();
        model.addAttribute("hostedEvents", hostedEvents);
        long issuedTickets = this.ticketService.issuedTickets();
        model.addAttribute("issuedTickets", issuedTickets);
        long eventCategories = this.eventCategoryService.countEventCategories();
        model.addAttribute("eventCategories", eventCategories);
        long activatedOrganizer = this.userServiceImpl.getActivatedOrganizers().size();
        model.addAttribute("activatedOrganizers", activatedOrganizer);
        List<EventSummaryDto> featuredEvents = this.eventService.findTopFeaturedEvents();
        model.addAttribute("featuredEvents", featuredEvents);
        List<FeaturedOrganizerDto> featuredOrganizers = this.userServiceImpl.getFeaturedOrganizers();
        model.addAttribute("featuredOrganizers", featuredOrganizers);
        FeaturedEventDTO featuredEvent = this.eventService.findFeaturedEvent();
        model.addAttribute("featuredEvent", featuredEvent);
        return "homepage/Home";
    }

}
