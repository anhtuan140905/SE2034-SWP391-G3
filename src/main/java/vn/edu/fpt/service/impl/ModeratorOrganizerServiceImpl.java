package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.configuration.PasswordEncoderConfig;
import vn.edu.fpt.model.OrganizerMember;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.UserRole;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.moderator.CreateOrganizerRequest;
import vn.edu.fpt.modelview.response.moderator.ModeratorOrganizerListDTO;
import vn.edu.fpt.modelview.response.moderator.OrganizerManagementStatsDTO;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.repository.UserRoleRepository;
import vn.edu.fpt.service.ModeratorOrganizerService;

@Service
@RequiredArgsConstructor
public class ModeratorOrganizerServiceImpl implements ModeratorOrganizerService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final EmailService emailService;

    @Override
    public void createOrganizerAccount(CreateOrganizerRequest request) {

        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        Role organizerRole = userRoleRepository.findByRoleName(RoleName.ROLE_ORGANIZER)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy ROLE_ORGANIZER"));

        Role attendeeRole = userRoleRepository.findByRoleName(RoleName.ROLE_ATTENDEE)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy ROLE_ATTENDEE"));

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPasswordHash(passwordEncoderConfig.passwordEncoder().encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setMiddleName(request.getMiddleName() != null && !request.getMiddleName().isBlank()
                ? request.getMiddleName().trim() : null);
        user.setLastName(request.getLastName().trim());
        user.setIsActive(true);

        UserRole userOrganizerRole = new UserRole();
        userOrganizerRole.setUser(user);
        userOrganizerRole.setRole(organizerRole);

        UserRole userAttendeeRole = new UserRole();
        userAttendeeRole.setUser(user);
        userAttendeeRole.setRole(attendeeRole);

        user.getUserRoles().add(userOrganizerRole);
        user.getUserRoles().add(userAttendeeRole);

        userRepository.save(user);

        StringBuilder organizerFullName = new  StringBuilder();
        if(request.getFirstName() != null && !request.getFirstName().isBlank()) {
            organizerFullName.append(request.getFirstName().trim());
        }
        if(request.getMiddleName() != null && !request.getMiddleName().isBlank()) {
            organizerFullName.append(request.getMiddleName().trim());
        }
        if(request.getLastName() != null && !request.getLastName().isBlank()) {
            organizerFullName.append(request.getLastName().trim());
        }

        String fullName = organizerFullName.toString().trim();

        emailService.sendOrganizerCredentialsEmail(request.getEmail(), fullName, request.getEmail(), request.getPassword());

    }

    @Override
    public Page<ModeratorOrganizerListDTO> getOrganizers(String keyword, String status, int page, int size) {

        String keywordString = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Boolean isActive = null;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findOrganizersByFilterAndSearch(RoleName.ROLE_ORGANIZER, keywordString, isActive, pageable).map(this::mapToDTO);

    }

    @Override
    public OrganizerManagementStatsDTO getOrganizerManagementStats() {

        OrganizerManagementStatsDTO stats = new OrganizerManagementStatsDTO();

        stats.setTotalOrganizers(userRepository.countAllOrganizers(RoleName.ROLE_ORGANIZER));

        stats.setTotalActiveOrganizers(userRepository.countOrganizersByStatus(RoleName.ROLE_ORGANIZER, true));

        stats.setTotalInactiveOrganizers(userRepository.countOrganizersByStatus(RoleName.ROLE_ORGANIZER, false));

        return stats;
    }

    private ModeratorOrganizerListDTO mapToDTO(User user) {
        ModeratorOrganizerListDTO dto = new ModeratorOrganizerListDTO();
        dto.setOrganizerId(user.getId());
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setJoinedDate(user.getCreatedAt());
        dto.setIsActive(user.getIsActive());

        return dto;
    }

    private String buildFullName(User user) {
        StringBuilder fullName = new StringBuilder();
        if (user.getFirstName() != null)
            fullName.append(user.getFirstName());
        if (user.getMiddleName() != null && !user.getMiddleName().isBlank())
            fullName.append(" ").append(user.getMiddleName());
        if (user.getLastName() != null && !user.getLastName().isBlank())
            fullName.append(" ").append(user.getLastName());

        return fullName.toString().trim();
    }
}





