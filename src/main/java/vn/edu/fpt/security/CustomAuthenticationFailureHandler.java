package vn.edu.fpt.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorCode;
        if(exception instanceof DisabledException) {
            errorCode = "not_activated";
        } else if(exception instanceof LockedException) {
            errorCode = "locked";
        } else if(exception instanceof BadCredentialsException) {
            errorCode = "bad_credentials";
        } else {
            errorCode = "unknown";
        }
        response.sendRedirect("/auth/login?error=" + errorCode);
    }
}
