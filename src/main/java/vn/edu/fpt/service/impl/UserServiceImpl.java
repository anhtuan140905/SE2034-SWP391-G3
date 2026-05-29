package vn.edu.fpt.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.common.error.CheckDuplicateException;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrganizerStatus;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.OrganizerProfileService;
import vn.edu.fpt.service.RoleService;
import vn.edu.fpt.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final OrganizerProfileService organizerProfileService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder,
                           OrganizerProfileRepository organizerProfileRepository,
                           OrganizerProfileService organizerProfileService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.organizerProfileRepository = organizerProfileRepository;
        this.organizerProfileService = organizerProfileService;
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
        String hashedPassword = this.passwordEncoder.encode(dto.getPassword());
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
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
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
}
