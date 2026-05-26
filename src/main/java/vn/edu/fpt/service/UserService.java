package vn.edu.fpt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final OrganizerProfileRepository organizerProfileRepository;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, OrganizerProfileRepository organizerProfileRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.organizerProfileRepository = organizerProfileRepository;
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
        Role role = this.roleService.getRoleByName("ROLE_ATTENDEE");
        user.setRole(role);
        String hashedPassword = this.passwordEncoder.encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);
        return this.userRepository.save(user);
    }
    public User handleCreateOrganizer(RegisterOrgDTO dto) {
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
        Role role = this.roleService.getRoleByName("ROLE_ORGANIZER");
        user.setRole(role);
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);
        User u =  this.userRepository.save(user);
        OrganizerProfile op = new OrganizerProfile();
        op.setUser(u);
        op.setStatus("PENDING");
        op.setBankAccount(dto.getBankAccount());
        op.setCompanyName(dto.getCompanyName());
        op.setTaxCode(dto.getTaxCode());
        this.organizerProfileRepository.save(op);
        return u;
    }
}
