package vn.edu.fpt.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.ModeratorOrganizerInformationDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.ModeratorOrganizerInformationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorOrganizerInformationServiceImpl implements ModeratorOrganizerInformationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public ModeratorOrganizerInformationDTO getOrganizerInformation(Long id) {

        User organizer = userRepository.findOrganizerInformationById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy organizer."));

        return mapToDTO(organizer);
    }

    @Override
    @Transactional
    public void deactivateOrganizer(Long id, String reason) {

        User user = userRepository.findOrganizerInformationById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy organizer."));

        OrganizerProfile profile = user.getOrganizerProfile();
        if (profile == null) {
            throw new RuntimeException("Organizer chưa có hồ sơ doanh nghiệp.");
        }

        if (profile.getIsActive() != null && !profile.getIsActive()) {
            throw new RuntimeException("Tài khoản Organizer đã bị khóa trước đó.");
        }

        profile.setIsActive(false);
        userRepository.save(user);

        String organizerEmail = user.getEmail();
        String organizerName = buildFullNameOrganizer(user);

        try {
            emailService.sendOrganizerDeactivateEmail(organizerEmail, organizerName, reason);
        } catch (MessagingException e) {
            log.error("Không thể gửi email khóa tài khoản cho organizerId={}, email={}", id, organizerEmail, e);
        }
    }

    @Override
    @Transactional
    public void activateOrganizer(Long id) {

        User user = userRepository.findOrganizerInformationById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy organizer."));

        OrganizerProfile profile = user.getOrganizerProfile();
        if (profile == null) {
            throw new RuntimeException("Organizer chưa có hồ sơ doanh nghiệp.");
        }

        if (profile.getIsActive() == null || profile.getIsActive()) {
            throw new RuntimeException("Tài khoản Organizer đang hoạt động, không cần mở lại.");
        }

        profile.setIsActive(true);
        userRepository.save(user);
    }

    private ModeratorOrganizerInformationDTO mapToDTO(User user) {
        ModeratorOrganizerInformationDTO dto = new ModeratorOrganizerInformationDTO();
        dto.setOrganizerId(user.getId());

        OrganizerProfile profile = user.getOrganizerProfile();

        boolean organizerProfileActive = profile != null && profile.getIsActive();
        dto.setActive(organizerProfileActive);
        dto.setHasOrganizerProfile(profile != null);
        dto.setFullName(buildFullNameOrganizer(user));
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());

        if(user.getAddress() != null) {
            dto.setHomeAddress(user.getAddress().getSpecificAddress());
            if(user.getAddress().getWard() != null) {
                dto.setWardName(user.getAddress().getWard().getName());
                if(user.getAddress().getWard().getCity() != null) {
                    dto.setCityName(user.getAddress().getWard().getCity().getName());
                }
            }
        }

        if(user.getOrganizerProfile() != null) {
            dto.setCompanyName(user.getOrganizerProfile().getCompanyName());
            dto.setTaxCode(user.getOrganizerProfile().getTaxCode());
            dto.setBusinessType(user.getOrganizerProfile().getBusinessType());
        }

        dto.setJoinedDate(user.getCreatedAt());
        dto.setTotalEventsOrganized(eventRepository.countEventOrganized(user.getId()));
        dto.setTotalInactiveEvents(eventRepository.countEventInactivated(user.getId(), EventStatus.INACTIVE));

        return dto;

    }

    private String buildFullNameOrganizer(User user) {

        StringBuilder stringBuilder = new StringBuilder();
        if(user.getFirstName() != null) {
            stringBuilder.append(user.getFirstName());
        }
        if(user.getMiddleName() != null && !user.getMiddleName().isBlank()) {
            stringBuilder.append(" ").append(user.getMiddleName());
        }
        if(user.getLastName() != null) {
            stringBuilder.append(" ").append(user.getLastName());
        }

        return stringBuilder.toString().trim();
    }
}
