package vn.edu.fpt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.admin.ActivityDTO;
import vn.edu.fpt.modelview.request.admin.UpdateUserStatusDTO;
import vn.edu.fpt.modelview.request.auth.RegisterUserDTO;
import vn.edu.fpt.modelview.request.auth.UpdateAttendeeProfileDTO;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    public User handleCreateUser(RegisterUserDTO dto);
    public User findByUsername(String username);
    public Optional<User> findByEmailWithRoles(String username);
    public User getUserById(Long id);
    public void handleUpdateUser(UpdateAttendeeProfileDTO dto, BindingResult result);
    public List<User> getAllUser();
    public User findById(Long id);
    public List<User> searchUser(String keyword);
    public List<User> getActivatedOrganizers();
    public List<FeaturedOrganizerDto> getFeaturedOrganizers();
    public User handleSaveUser(User user);
    public void updateUserStatus(Long id, Boolean isActive, Long currentUserId);
    public void removeRoleFromUser(Long userRoleId, Long targetUserId, Long currentUserId);
    public void addRoleToUser(Long targetUserId, String roleNameStr, Long currentUserId);
    public List<ActivityDTO> getUserActivities(Long userId);
    public String findCityNameByUserId(Long userId);
}