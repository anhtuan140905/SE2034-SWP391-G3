package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.configuration.PasswordEncoderConfig;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.UserRole;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.moderator.CreateOrganizerRequest;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.repository.UserRoleRepository;
import vn.edu.fpt.service.ModeratorOrganizerService;

@Service
@RequiredArgsConstructor
public class ModeratorOrganizerServiceImpl implements ModeratorOrganizerService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Override
    public void createOrganizerAccount(CreateOrganizerRequest request) {

        if(userRepository.existsByEmail(request.getEmail().trim())) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        Role organizerRole = userRoleRepository.findByRoleName(RoleName.ROLE_ORGANIZER)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy ROLE_ORGANIZER"));

        Role attendeeRole = userRoleRepository.findByRoleName(RoleName.ROLE_ATTENDEE)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy ROLE_ATTENDEE"));

        User user = new  User();
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

    }
}
