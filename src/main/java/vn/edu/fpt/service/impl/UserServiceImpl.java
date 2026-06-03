package vn.edu.fpt.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.common.error.CheckDuplicateException;
import vn.edu.fpt.configuration.PasswordEncoderConfig;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrganizerStatus;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final OrganizerProfileService organizerProfileService;
    private final WardService wardService;
    private final CityService cityService;
    private final CloudinaryService cloudinaryService;
    private final UserDetailsService userDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoderConfig passwordEncoderConfig,
                           OrganizerProfileRepository organizerProfileRepository,
                           OrganizerProfileService organizerProfileService,
                           WardService wardService,
                           CityService cityService,
                           CloudinaryService cloudinaryService,
                           @Lazy UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoderConfig = passwordEncoderConfig;
        this.organizerProfileRepository = organizerProfileRepository;
        this.organizerProfileService = organizerProfileService;
        this.wardService = wardService;
        this.cityService = cityService;
        this.cloudinaryService = cloudinaryService;
        this.userDetailsService = userDetailsService;
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username);
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
    public User handleCreateOrganizer(RegisterOrgDTO dto) throws CheckDuplicateException {
        if(findByUsername(dto.getUsername()) != null){
            throw new CheckDuplicateException("Email address has already been registered");
        }

        if(this.organizerProfileService.existsByTaxCode(dto.getTaxCode())){
            throw new CheckDuplicateException("Tax code has already been registered");
        }
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getUsername());
        user.setDob(dto.getDob());
        user.setIsActive(true);
        user.setPhone(dto.getPhone());
        Role role = this.roleService.getRoleByName(RoleName.ROLE_ORGANIZER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        String hashedPassword = this.passwordEncoderConfig.passwordEncoder().encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);
        User u =  this.userRepository.save(user);
        OrganizerProfile op = new OrganizerProfile();
        op.setUser(u);
        op.setStatus(OrganizerStatus.PENDING.toString());
        op.setBankAccount(dto.getBankAccount());
        op.setCompanyName(dto.getCompanyName());
        op.setTaxCode(dto.getTaxCode());
        this.organizerProfileRepository.save(op);
        return u;
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
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
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
}
