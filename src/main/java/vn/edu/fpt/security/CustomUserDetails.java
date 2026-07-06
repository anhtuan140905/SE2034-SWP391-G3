package vn.edu.fpt.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.service.AuthenticatedUser;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails, AuthenticatedUser {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
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
            authorities.add(new SimpleGrantedAuthority("ATTENDEE"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }

    @Override public Long getUserId() { return user.getId(); }
    @Override public String getEmail() { return user.getEmail(); }
    @Override public User getUser() { return user; }
}
