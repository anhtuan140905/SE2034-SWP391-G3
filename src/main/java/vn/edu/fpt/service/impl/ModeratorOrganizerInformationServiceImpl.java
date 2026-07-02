package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.ModeratorOrganizerInformationDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.ModeratorOrganizerInformationService;

@Service
@RequiredArgsConstructor
public class ModeratorOrganizerInformationServiceImpl implements ModeratorOrganizerInformationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ModeratorOrganizerInformationDTO getOrganizerInformation(Long id) {

        User organizer = userRepository.findOrganizerInformationById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy organizer."));

        return mapToDTO(organizer);
    }

    private ModeratorOrganizerInformationDTO mapToDTO(User user) {
        ModeratorOrganizerInformationDTO dto = new ModeratorOrganizerInformationDTO();
        dto.setOrganizerId(user.getId());
        dto.setActive(user.getIsActive());
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
