package vn.edu.fpt.controller.finance;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.modelview.response.finance.SettlementSummaryDTO;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.SettlementAgingProjection;
import vn.edu.fpt.repository.SettlementSummaryProjection;
import vn.edu.fpt.service.impl.*;
import vn.edu.fpt.security.CustomOAuth2User;
import vn.edu.fpt.security.CustomUserDetails;


import java.util.List;

@Controller
@RequestMapping("/finance")

public class FinanceController {
    private final UserServiceImpl userServiceImpl;
    private final EventServiceImpl eventServiceImpl;
    private final TicketServiceImpl ticketServiceImpl;
    private final CityServiceImpl cityServiceImpl;
    private final CloudinaryService cloudinaryService;
    private final SettlementServiceImpl settlementServiceImpl;


    public FinanceController(UserServiceImpl userServiceImpl,
                           EventServiceImpl eventServiceImpl,
                           TicketServiceImpl ticketServiceImpl,
                             CityServiceImpl cityServiceImpl,
                             CloudinaryService cloudinaryService,
                             SettlementServiceImpl settlementServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.eventServiceImpl = eventServiceImpl;
        this.ticketServiceImpl = ticketServiceImpl;
        this.cityServiceImpl = cityServiceImpl;
        this.cloudinaryService = cloudinaryService;
        this.settlementServiceImpl = settlementServiceImpl;
    }
   @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            @AuthenticationPrincipal CustomOAuth2User oAuth2Users){

       User currentUser = (userDetails != null)
               ? userServiceImpl.findByUsername(userDetails.getUsername())
               : userServiceImpl.findByUsername(oAuth2Users.getName());
       model.addAttribute("currentUser", currentUser);


       Long totalRevenue = eventServiceImpl.sumTotalRevenue();
       model.addAttribute("totalRevenue",totalRevenue);

       Long pendingPayoutAmount = settlementServiceImpl.sumPendingPayoutAmount();
       model.addAttribute("pendingPayoutAmount", pendingPayoutAmount);

       long nearDueCount = settlementServiceImpl.countNearDuePendingSettlements();
       model.addAttribute("nearDueCount", nearDueCount);

       Long totalPaidAmount = settlementServiceImpl.sumPayoutAmount();
       model.addAttribute("totalPaidAmount", totalPaidAmount);

       long unsettledEventCount = settlementServiceImpl.countUnsettledEvents();
       model.addAttribute("unsettledEventCount",unsettledEventCount);

       List<SettlementSummaryProjection> platformFeeByMonth = settlementServiceImpl.platformFeeByMonth();
       model.addAttribute("platformFeeByMonth",platformFeeByMonth);

       SettlementAgingProjection settlementAging = settlementServiceImpl.getSettlementAging();
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
            settlementServiceImpl.createSettlement(dto);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/finance/createSettlement?eventId=" + dto.getEventId();
        }

        return "redirect:/finance/listEndedEvents";
    }


   @GetMapping("/createSettlement")
    public String getCreateSettlementPage(Model model,
                                          String tab,
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                          @RequestParam(required = false) Long eventId){

       User currentUser = (userDetails != null)
               ? userServiceImpl.findByUsername(userDetails.getUsername())
               : userServiceImpl.findByUsername(oAuth2Users.getName());
       model.addAttribute("currentUser", currentUser);

       List<SettlementSummaryProjection> listEndedEvents = eventServiceImpl.findEndedEventsWithSettlementStatus(tab);
       model.addAttribute("listEndedEvents", listEndedEvents);

       SettlementSummaryProjection eventDetail =settlementServiceImpl.findEventDetailById(eventId);
       model.addAttribute("eventDetail",eventDetail);

       SettlementDTO dto = new SettlementDTO();
       dto.setEventId(eventId);

       model.addAttribute("settlementDTO", dto);


       return "finance/CreateSettlement";
   }

    @GetMapping("/listSettlement")
    public String listSettlementPage (Model model,
                                      @RequestParam(defaultValue = "all") String tab,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword){

        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        long countAllSettlement = settlementServiceImpl.countAllSettlement();
        model.addAttribute("countAllSettlement", countAllSettlement);

        long countPendingSettlement = settlementServiceImpl.countPendingSettlement();
        model.addAttribute("countPendingSettlement", countPendingSettlement);

        long countCompletedSettlement = settlementServiceImpl.countCompletedSettlement();
        model.addAttribute("countCompletedSettlement", countCompletedSettlement);

        Long totalPaidAmount = settlementServiceImpl.sumPayoutAmount();
        model.addAttribute("totalPaidAmount", totalPaidAmount);

        List<SettlementSummaryDTO> listSettlements;

        if (keyword != null && !keyword.trim().isEmpty()) {
            listSettlements = settlementServiceImpl.searchSettlement(keyword);
        } else {
            listSettlements = settlementServiceImpl.listSettlement(tab);
        }
        model.addAttribute("listSettlements", listSettlements);
        model.addAttribute("tab", tab);
        model.addAttribute("keyword", keyword);

        return "finance/ListSettlement";
    }

    @GetMapping("/viewSettlementDetails")
    public String viewSettlementDetailsPage (Model model,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                             @RequestParam Long settlementId){

        User currentUser = (userDetails != null)
                ? userServiceImpl.findByUsername(userDetails.getUsername())
                : userServiceImpl.findByUsername(oAuth2Users.getName());
        model.addAttribute("currentUser", currentUser);

        EventSummaryProjection eventDetail = eventServiceImpl.getEventDetail(settlementId);
        model.addAttribute("eventDetail", eventDetail);

        SettlementSummaryProjection settlementDetail = settlementServiceImpl.getSettlementDetail(settlementId);
        model.addAttribute("settlementDetail", settlementDetail);

        return "finance/ViewSettlementDetails";
    }

    @PostMapping("/settlements/{settlementId}/complete")
    public String completedSettlement(@PathVariable Long settlementId,
                                       RedirectAttributes redirectAttributes){
try{
       settlementServiceImpl.markAsCompleted(settlementId);
       redirectAttributes.addFlashAttribute("successMessage", "Đã Đánh Dấu Quyết Toán Thành Công");
}
catch (EntityNotFoundException | IllegalStateException e){
redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

}
return "redirect:/finance/viewSettlementDetails?settlementId=" + settlementId;
    }

   @GetMapping("/listEndedEvents")
    public String listEndedEventsPage (Model model,
                                       @RequestParam(defaultValue = "all") String tab,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       @AuthenticationPrincipal CustomOAuth2User oAuth2Users,
                                       @RequestParam(value = "keyword", defaultValue = "") String keyword){

       User currentUser = (userDetails != null)
               ? userServiceImpl.findByUsername(userDetails.getUsername())
               : userServiceImpl.findByUsername(oAuth2Users.getName());
       model.addAttribute("currentUser", currentUser);


       List<SettlementSummaryProjection> listEndedEvents;

       if (keyword != null && !keyword.trim().isEmpty()) {
           listEndedEvents = eventServiceImpl.searchEndedEvents(keyword);
       } else {
           listEndedEvents = eventServiceImpl.findEndedEventsWithSettlementStatus(tab);
       }
       model.addAttribute("listEndedEvents", listEndedEvents);
       model.addAttribute("tab", tab);
       model.addAttribute("keyword", keyword);


       long countEndEvent = eventServiceImpl.countEndedEvent();
       model.addAttribute("countEndEvent", countEndEvent);

       long unsettledEventCount = eventServiceImpl.countUnsettledEvents();
       model.addAttribute("unsettledEventCount",unsettledEventCount);

       long settledEventCount = settlementServiceImpl.countPendingSettlement();
       model.addAttribute("settledEventCount", settledEventCount);


       return "finance/ListEndedEvents";
   }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @AuthenticationPrincipal CustomOAuth2User oAuth2Users) {

        User user = new User();
        if(userDetails != null) {
            user = this.userServiceImpl.findByUsername(userDetails.getUsername());
        } else {
            user = this.userServiceImpl.findByUsername(oAuth2Users.getName());
        }
        UpdateAttendeeProfileDTO dto = new UpdateAttendeeProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setDob(user.getDob());
        if(user.getAddress() != null){
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId()));
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }

        model.addAttribute("cities", this.cityServiceImpl.getCityList());
        model.addAttribute("userUpdateDTO", dto);
        return "homepage/UpdateProfileUser";
    }


    @PostMapping("/update/profile")
    public String updateProfile(
            Model model,
            @Valid @ModelAttribute UpdateAttendeeProfileDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        try {
            if(avatarFile != null && !avatarFile.isEmpty()){
                String imageUrl = this.cloudinaryService.uploadFile(avatarFile, "avatars");
                dto.setAvatar(imageUrl);
            } else {
                dto.setAvatar(null);
            }
            this.userServiceImpl.handleUpdateUser(dto, result);
        } catch (Exception e) {
            model.addAttribute("cities", this.cityServiceImpl.getCityList());
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("userUpdateDTO", dto);
            return "homepage/UpdateProfileUser";
        }
        return "redirect:/finance/profile";
    }



}
