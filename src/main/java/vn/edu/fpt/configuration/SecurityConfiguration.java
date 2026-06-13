package vn.edu.fpt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import vn.edu.fpt.service.impl.security.CustomAuthenticationFailureHandler;
import vn.edu.fpt.service.impl.security.CustomDaoAuthenticationProvider;
import vn.edu.fpt.service.impl.security.CustomOAuth2UserService;
import vn.edu.fpt.service.impl.security.CustomUserDetailsService;

import java.util.List;


@Configuration
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService,
                                 PasswordEncoderConfig passwordEncoderConfig,
                                 CustomOAuth2UserService customOAuth2UserService,
                                 ClientRegistrationRepository clientRegistrationRepository,
                                 CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoderConfig = passwordEncoderConfig;
        this.customOAuth2UserService = customOAuth2UserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2RequestResolver() {
        var resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(c ->
                c.additionalParameters(p -> p.put("prompt", "select_account")));
        return resolver;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        CustomDaoAuthenticationProvider authenticationProvider = new CustomDaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(this.passwordEncoderConfig.passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPreAuthenticationChecks(u -> {}); // nhận user nhưng bỏ qua
                                                            // Thằng này nó nhận vafo 1 thằng UserDetailsChecker mà là interface có đúng 1 hàm duy nhất nên viết lambda đc
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
                                "/", "/events", "/auth/**", "/css/**",
                                "/js/**", "/homepage/**",
                                "/auth/css/**", "/auth/js/**"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/moderator/**").hasAuthority("ROLE_MODERATOR")
                        .requestMatchers("/finance/**").hasAuthority("ROLE_FINANCE")
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
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login").authorizationEndpoint(a -> a
                                        .authorizationRequestResolver(oAuth2RequestResolver()))
                .userInfoEndpoint(u -> u
                        .userService(customOAuth2UserService)
                )
                .successHandler(customSuccessHandler())
                .failureUrl("/auth/login?error=oauth2")
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
