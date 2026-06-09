package vn.edu.fpt.controller.moderator;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.repository.EventCategoryRepository;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.service.impl.EmailService;
import vn.edu.fpt.service.impl.EventServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/moderator")
@AllArgsConstructor
public class ModeratorEventController {

    private final EventRepository eventRepository;
    private final EventServiceImpl eventService;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EmailService emailService;

    @GetMapping("/events")
    public String listEvents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String statusStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {

        // 1. Phân trang
        Pageable pageable = PageRequest.of(page, 10, Sort.by("eventId").descending());

        // 2. Chuyển đổi kiểu String từ filter sang định dạnh Enum
        EventStatus status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = EventStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                status = null;
            }
        }

        Page<Event> eventPage = eventRepository.searchAndFilterEvents(keyword, status, categoryId, pageable);

        // 4. Đếm số lượng động để hiển thị lên 4 ô Thống kê (Stat Cards)
        Map<String, Long> stats = new HashMap<>();
        stats.put("awaitingReview", eventRepository.countByStatus(EventStatus.PENDING));
        stats.put("activeEvents", eventRepository.countByStatus(EventStatus.APPROVED));
        stats.put("approvedToday", 0L);
        stats.put("rejectedToday", 0L);

        // 5. pull data lên trang
        model.addAttribute("events", eventPage);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", statusStr);
        model.addAttribute("eventStats", stats);

        return "/moderator/EventManagement";
    }

    // -----------------------------------------------------------------------------------------------
    @GetMapping("/information/{id}")
    public String organizerInformation(
            @PathVariable("id") Long organizerId,
            @RequestParam(value = "fromEvent", required = false) Long eventId,
            Model model
    ) {
        // 1. Lấy data từ Database
        OrganizerProfile organizerProfile = organizerProfileRepository.findById(organizerId).orElse(null);

        // 2. Mapping dữ liệu khớp với HTML
        if (organizerProfile != null && organizerProfile.getUser() != null) {
            Map<String, Object> orgDto = new HashMap<>();

            // Thông tin cá nhân (Lấy từ bảng User)
            orgDto.put("avatarUrl", organizerProfile.getUser().getAvatar());
            orgDto.put("fullName", organizerProfile.getUser().getFirstName() + " " + organizerProfile.getUser().getLastName());
            orgDto.put("email", organizerProfile.getUser().getEmail());

            // Thông tin công ty (Lấy từ bảng OrganizerProfile)
            orgDto.put("companyName", organizerProfile.getCompanyName());
            orgDto.put("taxId", organizerProfile.getTaxCode()); // Entity là taxCode, HTML là taxId

            // -- Các trường địa chỉ và thống kê (Tạm mock data để lên hình đẹp, bạn có thể gọi DB thật sau) --
            orgDto.put("joinedDate", java.time.LocalDate.now());
            orgDto.put("homeAddress", "Tòa nhà FPT Polytechnic");
            orgDto.put("ward", "Khu công nghệ cao");
            orgDto.put("city", "Hồ Chí Minh");
            orgDto.put("totalEventsOrganized", 5);
            orgDto.put("totalRejectedEvents", 1);

            // Đẩy Map này lên giao diện
            model.addAttribute("organizer", orgDto);
        } else {
            // Không tìm thấy thì trả về null để HTML hiện cô gái "Helena Carter"
            model.addAttribute("organizer", null);
        }

        // 3. Truyền ID sự kiện để làm nút Back
        model.addAttribute("backEventId", eventId);

        return "moderator/OrganizerInformation";
    }


    @GetMapping("/event/detail/{id}")
    public String eventDetail(@PathVariable("id") Long id, Model model) {
        try {
            EventDetailModeratorDTO eventDTO = eventService.getEventDetailById(id);

            model.addAttribute("event", eventDTO);

        } catch (Exception e) {
            model.addAttribute("event", null);
        }

        return "moderator/EventDetail";
    }

    @PostMapping("/events/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveEventAPI(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> payload
    ) {
        try {
            Event event = eventRepository.findById(id).orElse(null);
            if (event != null) {
                event.setStatus(EventStatus.APPROVED);
                eventRepository.save(event);

                // Lấy lời nhắn gõ từ ô Textarea
                String reviewMessage = payload.get("message");
                String toEmail = event.getOrganizer().getEmail();
                String organizerName = event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName();
                String eventTitle = event.getTitle();

                emailService.sendEventApprovalEmail(toEmail, organizerName, eventTitle, reviewMessage);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/events/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectEventAPI(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> payload
    ) {
        try {
            Event event = eventRepository.findById(id).orElse(null);
            if (event != null) {
                event.setStatus(EventStatus.REJECTED);
                eventRepository.save(event);

                // Lấy lý do từ chối
                String reviewMessage = payload.get("message");
                String toEmail = event.getOrganizer().getEmail();
                String organizerName = event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName();
                String eventTitle = event.getTitle();

                emailService.sendEventRejectionEmail(toEmail, organizerName, eventTitle, reviewMessage);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
