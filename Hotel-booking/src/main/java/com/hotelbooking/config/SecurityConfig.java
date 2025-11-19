package com.hotelbooking.config;

import com.hotelbooking.service.DbUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("unused")
@Configuration
@Profile("!permitall")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final DbUserDetailsService uds;

    public SecurityConfig(DbUserDetailsService uds) {
        this.uds = uds;
    }

    // Password encryption (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Ensure only one UserDetailsService bean is used (disables Boot's default in-memory user)
    @Bean
    public UserDetailsService userDetailsService() {
        return uds;
    }

    // DAO-based authentication provider
    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // HTTP security rules
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CSRF for form-based login flows
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))

            // URL authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/login", "/register", "/auth/**",
                        "/css/**", "/js/**", "/images/**", "/static/**", "/uploads/**", "/webjars/**", "/favicon.ico", "/error", "/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                // Let controller handle redirecting unauthenticated users so they see the login page
                .requestMatchers(HttpMethod.POST, "/rooms/*/book").permitAll()
                .requestMatchers("/payments/**").hasRole("USER")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())

            // Form login for Thymeleaf pages
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    String next = request.getParameter("next");
                    if (next != null && !next.isBlank() && next.startsWith("/")) {
                        response.sendRedirect(next);
                        return;
                    }
                    response.sendRedirect("/rooms");
                })
                .failureUrl("/login?error")
                .permitAll())

            // Logout handler with session invalidation
            .logout(logout -> logout
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout")
            )

            // Allow H2 console to render in frames during dev profiles
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            // Connect DB-based provider
            .authenticationProvider(daoAuthProvider());

        return http.build();
    }

    // Authentication Manager bean (used in login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
