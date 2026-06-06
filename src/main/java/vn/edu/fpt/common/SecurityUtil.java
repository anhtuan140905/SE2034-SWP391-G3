package vn.edu.fpt.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.User;


public class SecurityUtil {
    public static String getCurrentUsername(){
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated()){
            Object principal = auth.getPrincipal();

            if(principal instanceof UserDetails){
                String username = ((UserDetails) principal).getUsername();
                return username;
            }
        }
        return null;
    }
}
