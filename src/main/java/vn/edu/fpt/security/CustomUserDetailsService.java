package vn.edu.fpt.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.service.UserService;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.edu.fpt.model.User user = this.userService.findByEmailWithRoles(username).orElse(null);
        if(user == null){
            throw new UsernameNotFoundException("Không tìm thấy USER");
        }
        return new CustomUserDetails(user);
    }
}
