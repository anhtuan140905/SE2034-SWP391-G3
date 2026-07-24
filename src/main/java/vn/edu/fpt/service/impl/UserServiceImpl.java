package vn.edu.fpt.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.exception.ServiceValidationException;
import vn.edu.fpt.configuration.PasswordEncoderConfig;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.admin.ActivityDTO;
import vn.edu.fpt.modelview.request.admin.UpdateUserStatusDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service("UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final OrganizerProfileService organizerProfileService;
    private final WardService wardService;
    private final VerifyTokenService verifyTokenService;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoderConfig passwordEncoderConfig,
                           OrganizerProfileRepository organizerProfileRepository,
                           OrganizerProfileService organizerProfileService,
                           WardService wardService,
                           CloudinaryService cloudinaryService,
                           @Lazy UserDetailsService userDetailsService,
                           EmailService emailService,
                           VerifyTokenService verifyTokenService,
                           EventRepository eventRepository,
                           OrderRepository orderRepository,
                           TicketRepository ticketRepository,
                           UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoderConfig = passwordEncoderConfig;
        this.organizerProfileRepository = organizerProfileRepository;
        this.organizerProfileService = organizerProfileService;
        this.wardService = wardService;
        this.verifyTokenService = verifyTokenService;
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.userRoleRepository = userRoleRepository;
    }


    public User findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User handleCreateUser(RegisterUserDTO dto) {
        ServiceValidationException ex = new ServiceValidationException();

        if(this.findByUsername(dto.getUsername()) != null) {
            ex.add("username", "Email đã tồn tại!");
        }

        if(dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
            ex.add("confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        if (dto.getDob() != null) {
            if (dto.getDob().isAfter(LocalDate.now())) {
                ex.add("dob", "Ngày sinh không được ở trong tương lai");
            } else if (Period.between(dto.getDob(), LocalDate.now()).getYears() < 13) {
                ex.add("dob", "Bạn phải đủ 13 tuổi để đăng ký");
            }
        }

        if (ex.hasErrors()) {
            throw ex;
        }

        User user = new User();
        user.setFirstName(dto.getFirstName().trim());
        user.setMiddleName(dto.getMiddleName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setEmail(dto.getUsername());
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());
        user.setIsActive(true);
        user.setPhone(normalizePhone(dto.getPhone()));
        String hashedPassword = passwordEncoderConfig.passwordEncoder().encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);
        User savedUser = userRepository.save(user);

        // Thay set<Role> bằng UserRole
        Role role = roleService.getRoleByName(RoleName.ROLE_ATTENDEE);
        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .build();
        userRoleRepository.save(userRole);

        return savedUser;
    }
    private String normalizePhone(String phone) {
        return phone.startsWith("+84") ? "0" + phone.substring(3) : phone;
    }

    @Override
    public Optional<User> findByEmailWithRoles(String username) {
        return userRepository.findByEmailWithUserRoles(username);
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override 
    public void handleUpdateUser(UpdateAttendeeProfileDTO dto, BindingResult result) {
        User user = this.findByUsername(dto.getEmail());

        if (user == null) {
            return;
        }

        boolean hasPassword = dto.getPassword() != null && !dto.getPassword().isBlank();
        boolean hasConfirmPassword = dto.getConfirmPassword() != null && !dto.getConfirmPassword().isBlank();
        boolean hasOldPassword = dto.getOldPassword() != null && !dto.getOldPassword().isBlank();

        if (hasPassword ||  hasConfirmPassword || hasOldPassword ) {
            boolean userHasPasswordInDb = user.getPasswordHash() != null && !user.getPasswordHash().isEmpty();

            if (userHasPasswordInDb) {
                if (!hasOldPassword) {
                    result.rejectValue("oldPassword", "error.oldPassword", "Vui long nhập mật khẩu hiện tại");
                } else if (!this.passwordEncoderConfig.passwordEncoder().matches(dto.getOldPassword(), user.getPasswordHash())) {
                    result.rejectValue("oldPassword", "error.oldPassword", "Mâtj khẩu hiện tại không đúng");
                }
            }

            if (!hasPassword) {
                result.rejectValue("password", "error.password", "Vui lòng nhập mật khẩu mới");
            }

            if (!hasConfirmPassword) {
                result.rejectValue("confirmPassword", "error.confirmPassword", "Vui lòng nhập lại mật khâu mới");
            }

            if (hasPassword && hasConfirmPassword) {
                if (dto.getPassword().length() < 8) {
                    result.rejectValue("password", "error.password", "Mật khẩu mới phải nhiều hơn 8 ký tự");
                }

                if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Không đúng với mật khẩu mới");
                }
            }

            if (result.hasErrors()) {
                return;
            }

            user.setPasswordHash(this.passwordEncoderConfig.passwordEncoder().encode(dto.getPassword()));
        }

        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());

        if (dto.getWard() != null && !dto.getWard().isBlank()) {

            Ward ward = this.wardService.findById(Long.parseLong(dto.getWard()));

            Address address = user.getAddress();
            if (address != null) {
                address.setWard(ward);
                address.setSpecificAddress(dto.getSpecificAddress());
            } else {
                address = new Address();
                address.setWard(ward);
                address.setSpecificAddress(dto.getSpecificAddress());
            }

            user.setAddress(address);
        }

        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        this.userRepository.save(user);
    }

    @Override
    public UpdateAttendeeProfileDTO getProfileDTOByEmail(String email) {
        User user = this.findByUsername(email);
        if (user == null) {
            return null;
        }

        UpdateAttendeeProfileDTO dto = new UpdateAttendeeProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());
        dto.setDob(user.getDob());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());

        if (user.getAddress() != null) {
            dto.setCity(String.valueOf(user.getAddress().getWard().getCity().getId()));
            dto.setWard(String.valueOf(user.getAddress().getWard().getId()));
            dto.setSpecificAddress(user.getAddress().getSpecificAddress());
        }

        return dto;
    }

    public List<User> getAllUser() {

        return userRepository.findAll();
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));
    }

    public List<User> searchUser(String keyword) {
        return userRepository.seachUser(keyword);
    }

    @Override
    public List<User> getActivatedOrganizers() {
        return this.userRepository.findActiveOrganizers();
    }

    @Override
    public List<FeaturedOrganizerDto> getFeaturedOrganizers() {
        List<FeaturedOrganizerDto> top3 = this.userRepository.getTopFeaturedOrganizer((Pageable) PageRequest.of(0, 3));
        return top3;
    }

    @Transactional
    public void updateUserStatus(Long id, Boolean isActive, Long currentUserId){

        if (currentUserId.equals(id)) {
            throw new RuntimeException("Bạn không thể tự chỉnh sửa tài khoản của chính mình.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        user.setIsActive(isActive);
    }

    private static final Set<RoleName> ADMIN_MANAGEABLE_ROLES = Set.of(
            RoleName.ROLE_ATTENDEE,
            RoleName.ROLE_MODERATOR,
            RoleName.ROLE_FINANCE
    );

    @Transactional
    public void removeRoleFromUser(Long userRoleId, Long targetUserId, Long currentUserId) {

        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Bạn không thể tự chỉnh sửa tài khoản của chính mình.");
        }

        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò cần xoá."));

        if (!userRole.getUser().getId().equals(targetUserId)) {
            throw new RuntimeException("Vai trò này không thuộc về người dùng đang chỉnh sửa.");
        }

        RoleName roleName = userRole.getRole().getRoleName();
        if (!ADMIN_MANAGEABLE_ROLES.contains(roleName)) {
            throw new RuntimeException("Vai trò " + roleName + " không thể chỉnh sửa qua giao diện này.");
        }

        long remainingRoles = userRoleRepository.countByUser_Id(targetUserId);
        if (remainingRoles <= 1) {
            throw new RuntimeException("Người dùng phải có ít nhất 1 vai trò, không thể xoá vai trò cuối cùng.");

        }


        userRoleRepository.delete(userRole);

    }

    @Transactional
    public void addRoleToUser(Long targetUserId, String roleNameStr, Long currentUserId) {

        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Bạn không thể tự chỉnh sửa tài khoản của chính mình.");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(roleNameStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Vai trò không hợp lệ.");
        }

        if (!ADMIN_MANAGEABLE_ROLES.contains(roleName)) {
            throw new RuntimeException("Không thể gán vai trò " + roleName + " qua giao diện này.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        boolean alreadyHasThisRole = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getRoleName() == roleName);

        if (alreadyHasThisRole) {
            throw new RuntimeException("Người dùng đã có vai trò này rồi, không thể thêm trùng.");
        }

        Role role = roleService.getRoleByName(roleName);

        UserRole newUserRole = new UserRole();
        newUserRole.setUser(user);
        newUserRole.setRole(role);

        userRoleRepository.save(newUserRole);
    }

    @Override
    public List<ActivityDTO> getUserActivities(Long userId) {

        List<ActivityDTO> result = new ArrayList<>();

        User user = findById(userId);

        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName().name())
                .collect(Collectors.toSet());

        if (user.getUpdatedAt() != null) {
            ActivityDTO profile = new ActivityDTO();
            profile.setAction("PROFILE_UPDATED");
            profile.setDescription("Cập nhật hồ sơ cá nhân");
            profile.setTime(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneId.systemDefault()));
            profile.setReferenceId(String.valueOf(user.getId()));
            result.add(profile);
        }
        // ================= ATTENDEE =================
        if (roles.contains("ROLE_ATTENDEE")) {

            List<Order> orders = orderRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);

            for (Order o : orders) {
                ActivityDTO dto = new ActivityDTO();
                dto.setAction("TICKET_PURCHASED");
                dto.setDescription("Đã mua vé sự kiện " + o.getEvent().getTitle());

                if (o.getCreatedAt() != null) {
                    dto.setTime(LocalDateTime.ofInstant(o.getCreatedAt(), ZoneId.systemDefault()));
                } else {
                    dto.setTime(null);
                }

                dto.setReferenceId(String.valueOf(o.getOrderId()));
                result.add(dto);
            }


        }

        // ================= ORGANIZER =================
        else if (roles.contains("ROLE_ORGANIZER")) {

            List<Event> events = eventRepository.findTop10ByOrganizerIdOrderByCreatedAtDesc(userId);

            for (Event e : events) {
                ActivityDTO dto = new ActivityDTO();
                dto.setAction("EVENT_CREATED");
                dto.setDescription("Đã tạo sự kiện " + e.getTitle());

                if (e.getCreatedAt() != null) {
                    dto.setTime(LocalDateTime.ofInstant(e.getCreatedAt(), ZoneId.systemDefault()));
                } else {
                    dto.setTime(null);
                }

                dto.setReferenceId(String.valueOf(e.getEventId()));
                result.add(dto);
            }

            List<Order> orders = orderRepository.findTop10ByEvent_OrganizerIdOrderByCreatedAtDesc(userId);

            for (Order o : orders) {
                ActivityDTO dto = new ActivityDTO();
                dto.setAction("TICKET_SOLD");
                dto.setDescription("Có người mua vé sự kiện " + o.getEvent().getTitle());

                if (o.getCreatedAt() != null) {
                    dto.setTime(LocalDateTime.ofInstant(o.getCreatedAt(), ZoneId.systemDefault()));
                } else {
                    dto.setTime(null);
                }

                dto.setReferenceId(String.valueOf(o.getOrderId()));
                result.add(dto);
            }
        }

        // ================= ADMIN =================
        else if (roles.contains("ROLE_ADMIN")) {

            List<User> users = userRepository.findTop10ByOrderByUpdatedAtDesc();

            for (User u : users) {
                ActivityDTO dto = new ActivityDTO();

                if (Boolean.TRUE.equals(u.getIsActive())) {
                    dto.setAction("USER_UPDATED");
                    dto.setDescription("Cập nhật user " + u.getEmail());
                } else {
                    dto.setAction("USER_LOCKED");
                    dto.setDescription("Khóa tài khoản " + u.getEmail());
                }

                if (u.getCreatedAt() != null) {
                    dto.setTime(LocalDateTime.ofInstant(u.getCreatedAt(), ZoneId.systemDefault()));
                } else {
                    dto.setTime(null);
                }

                dto.setReferenceId(String.valueOf(u.getId()));
                result.add(dto);
            }
        }

        // ================= MODERATOR =================
        else if (roles.contains("ROLE_MODERATOR")) {

            List<User> organizers = userRepository.findTop10ByRoleNameOrderByUpdatedAtDesc(RoleName.ROLE_ORGANIZER);

            for (User u : organizers) {

                ActivityDTO dto = new ActivityDTO();

                if (Boolean.TRUE.equals(u.getIsActive())) {
                    dto.setAction("ORGANIZER_APPROVED");
                    dto.setDescription("Duyệt organizer: " + u.getEmail());
                } else {
                    dto.setAction("ORGANIZER_REJECTED");
                    dto.setDescription("Từ chối organizer: " + u.getEmail());
                }


                if (u.getCreatedAt() != null) {
                    dto.setTime(LocalDateTime.ofInstant(u.getCreatedAt(), ZoneId.systemDefault()));
                } else {
                    dto.setTime(null);
                }

                dto.setReferenceId(String.valueOf(u.getId()));
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public String findCityNameByUserId(Long userId) {
        return this.userRepository.findCityNameByUserId(userId).orElse(null);
    }

}