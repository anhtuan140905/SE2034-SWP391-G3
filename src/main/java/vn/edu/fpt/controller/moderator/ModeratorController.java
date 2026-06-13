package vn.edu.fpt.controller.moderator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.constant.OrganizerStatus;
import vn.edu.fpt.repository.OrganizerProfileRepository;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    @GetMapping("/profile")
    public String moderatorProfile() {
        return  "moderator/ModeratorProfile";
    }

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

//    @GetMapping("/organizers")
//    public String getOrganizerApprovals(
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "status", required = false) String status,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            Model model) {
//
//        OrganizerStatus organizerStatus = null;
//        if (status != null && !status.trim().isEmpty()) {
//            try {
//                organizerStatus = OrganizerStatus.valueOf(status.trim().toUpperCase());
//            } catch (IllegalArgumentException e) {
//                // Tránh crash hệ thống nếu người dùng tự ý sửa bậy status trên URL
//            }
//        }
//
//        Pageable pageable = PageRequest.of(page, 10);
//
//        Page<OrganizerProfile> organizers = organizerProfileRepository.searchAndFilterOrganizers(keyword, status, pageable);
//
//        model.addAttribute("organizers", organizers);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("statusFilter", status);
//
//        return "moderator/OrganizerApproval";
//
//    }

}