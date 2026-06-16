package vn.edu.fpt.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.configuration.PasswordEncoderConfig;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.*;

import java.util.*;

@Service("UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final OrganizerProfileService organizerProfileService;
    private final WardService wardService;
    private final VerifyTokenService  verifyTokenService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoderConfig passwordEncoderConfig,
                           OrganizerProfileRepository organizerProfileRepository,
                           OrganizerProfileService organizerProfileService,
                           WardService wardService,
                           CloudinaryService cloudinaryService,
                           @Lazy UserDetailsService userDetailsService,
                           EmailService emailService,
                           VerifyTokenService verifyTokenService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoderConfig = passwordEncoderConfig;
        this.organizerProfileRepository = organizerProfileRepository;
        this.organizerProfileService = organizerProfileService;
        this.wardService = wardService;
        this.verifyTokenService = verifyTokenService;
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username);
    }
    @Override
    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }


    public User handleCreateUser(RegisterUserDTO dto) {
        if(findByUsername(dto.getUsername()) != null){
            return null;
        }
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getUsername());
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());
        user.setIsActive(true);
        user.setPhone(dto.getPhone());
        Role role = this.roleService.getRoleByName(RoleName.ROLE_ATTENDEE);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        String hashedPassword = this.passwordEncoderConfig.passwordEncoder().encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);
        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmailWithRoles(String username) {
        return this.userRepository.findByEmailWithRoles(username);
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public void handleUpdateUser(UpdateAttendeeProfileDTO dto, BindingResult result) {
        User user = this.findByUsername(dto.getEmail());
        if(user == null){
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
        Ward ward = this.wardService.findById(Long.parseLong(dto.getWard()));
        Address address = user.getAddress();
        if(address != null){
            address.setWard(ward);
            address.setSpecificAddress(dto.getSpecificAddress());
        } else {
            address = new Address();
            address.setWard(ward);
            address.setSpecificAddress(dto.getSpecificAddress());
        }
        user.setAddress(address);
        if(dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        this.userRepository.save(user);
    }

    public List<User> getAllUser() {

        return userRepository.findAll();
    }
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found: " + id));
    }

    public List<User> searchUser(String keyword){
        return userRepository.findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword,keyword,keyword);
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


}
