package vn.edu.fpt.service.impl.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        // T cho nó check pass trước để chống mò
        super.additionalAuthenticationChecks(userDetails, usernamePasswordAuthenticationToken);

        if(!userDetails.isEnabled()) {
            throw new DisabledException("Tài khoản chưa được kích hoạt");
        }
    }


}
