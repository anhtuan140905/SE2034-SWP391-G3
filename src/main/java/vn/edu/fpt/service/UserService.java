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
public interface UserService {

    public User handleCreateUser(RegisterUserDTO dto);
    public User findByUsername(String username);
    public User handleCreateOrganizer(RegisterOrgDTO dto);
}
