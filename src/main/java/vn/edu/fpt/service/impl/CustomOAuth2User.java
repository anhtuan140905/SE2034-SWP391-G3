package vn.edu.fpt.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.RoleName;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User {
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
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().toString()))
                .collect(Collectors.toSet());

        boolean isOrganizer = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_ORGANIZER);

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

    public User getUser() {
        return user;
    }
}
