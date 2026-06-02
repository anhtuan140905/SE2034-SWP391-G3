package vn.edu.fpt.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.RoleService;
import vn.edu.fpt.service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   RoleService roleService,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi Google/Facebook API lấy attributes
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" | "facebook"
        String email = extractEmail(registrationId, attributes);

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Không lấy được email từ " + registrationId);
        }

        // Tìm hoặc tạo mới user
        User user = this.userRepository.findByEmailWithRoles(email) // query FETCH JOIN roles
                .orElseGet(() -> createOAuth2User(email, attributes, registrationId));

        return new CustomOAuth2User(user, attributes);
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        // Google trả email thẳng
        // Facebook trả email trong attributes nếu đã grant scope
        return (String) attributes.get("email");
    }

    private User createOAuth2User(String email, Map<String, Object> attributes, String registrationId) {
        User user = new User();
        user.setEmail(email);
        user.setIsActive(true);
        user.setPasswordHash(null); // OAuth2 user không có password

        // Lấy tên từ attributes
        if ("google".equals(registrationId)) {
            user.setFirstName((String) attributes.getOrDefault("given_name", ""));
            user.setLastName((String) attributes.getOrDefault("family_name", ""));
            user.setAvatar((String) attributes.getOrDefault("picture", ""));

        } else if ("facebook".equals(registrationId)) {
            String name = (String) attributes.getOrDefault("name", "");
            user.setFirstName(name);
        }
        Role role = roleService.getRoleByName(RoleName.ROLE_ATTENDEE);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}