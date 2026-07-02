package vn.edu.fpt.service.impl.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.service.AuthenticatedUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User, AuthenticatedUser, Serializable {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(
                        userRole.getRole().getRoleName().toString()))
                .collect(Collectors.toSet());

        boolean isOrganizer = user.getUserRoles().stream()
                .anyMatch(userRole ->
                        userRole.getRole().getRoleName() == RoleName.ROLE_ORGANIZER);


        if (isOrganizer) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ATTENDEE"));
        }

        return authorities;
    }

    // OAuth2User.getName() — Spring dùng field này làm principal name
    @Override
    public String getName() {
        return user.getEmail();
    }
    @Override public Long getUserId() { return user.getId(); }
    @Override public String getEmail() { return user.getEmail(); }
    @Override public User getUser() { return user; }
}
