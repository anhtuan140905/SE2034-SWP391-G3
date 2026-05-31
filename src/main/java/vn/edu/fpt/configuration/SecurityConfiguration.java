package vn.edu.fpt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.impl.CustomUserDetailsService;

import java.util.List;


@Configuration
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService, PasswordEncoderConfig passwordEncoderConfig) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(this.passwordEncoderConfig.passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        return authenticationProvider;
    }
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList();

            String redirect = "/";
            if      (roles.contains("ROLE_ADMIN"))     redirect = "/admin/dashboard";
            else if (roles.contains("ROLE_MODERATOR")) redirect = "/moderator/dashboard";
            else if (roles.contains("ROLE_FINANCE"))   redirect = "/finance/dashboard";
            else if (roles.contains("ROLE_ORGANIZER")) redirect = "/organizer/dashboard";
            else if (roles.contains("ROLE_STAFF"))     redirect = "/staff/dashboard";
            else                                        redirect = "/";
            response.sendRedirect(request.getContextPath() + redirect);
        };
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("eventhub-secret-key", customUserDetailsService);
        rememberMe.setTokenValiditySeconds(7 * 24 * 60 * 60); // 7 ngày
        rememberMe.setParameter("remember-me");
        return rememberMe;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/auth/**", "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/moderator/**").hasAuthority("ROLE_MODERATOR")
                        .requestMatchers("/finance/**").hasAuthority("ROLE_FINANCE")
                        .requestMatchers("/organizer/**").hasAuthority("ROLE_ORGANIZER")
                        .requestMatchers("/staff/**").hasAuthority("ROLE_STAFF")

                        .requestMatchers("/ticket/**", "/orders/**").hasAuthority("ROLE_ATTENDEE")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/auth/login?expired=true")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(customSuccessHandler())
                        .failureForwardUrl("/auth/login?error=true")
                        .permitAll()
                )
                .rememberMe(r -> r.rememberMeServices(rememberMeServices()))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();

    }
}
