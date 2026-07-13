package vn.edu.fpt.service.impl;

import io.micrometer.common.lang.NonNull;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.common.error.ServiceValidationException;
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

import vn.edu.fpt.model.constant.TicketStatus;

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
        if (dto.getPassword() != null && !dto.getPassword().isBlank() && dto.getConfirmPassword() != null && !dto.getConfirmPassword().isBlank()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
            }
            if (dto.getPassword().length() < 8) {
                result.rejectValue("password", "error.password", "Mật khẩu phải có ít nhất 8 ký tự");
            }
            if (result.hasErrors()) return;
            user.setPasswordHash((this.passwordEncoderConfig.passwordEncoder().encode(dto.getPassword())));

        }
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        if (!dto.getWard().isBlank() || !dto.getWard().isEmpty() || !dto.getWard().equals("")) {


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

    public List<User> getAllUser() {

        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));
    }

    public List<User> searchUser(String keyword) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword, keyword);
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
    public void updateUser(Long id,
                           UpdateUserStatusDTO request,
                           Long currentUserId){

        if (currentUserId.equals(id)) {
            throw new RuntimeException("You cannot modify your own account.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleService.getRoleByName(
                RoleName.valueOf(request.getRoleName())
        );

        UserRole userRole = userRoleRepository.findByUser_Id(id)
                .orElseThrow(() -> new RuntimeException("UserRole not found"));


        userRole.setRole(role);

        
        user.setIsActive(request.getIsActive());
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