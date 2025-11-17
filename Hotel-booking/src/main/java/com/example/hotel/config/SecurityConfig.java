package com.example.hotel.config;

import com.example.hotel.service.DbUserDetailsService;
import com.example.hotel.config.JwtAuthFilter;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("unused")
@Configuration
@Profile("!permitall")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final DbUserDetailsService uds;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(DbUserDetailsService uds, JwtAuthFilter jwtAuthFilter) {
        this.uds = uds;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Password encryption (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
            // Disable CSRF (since JWT is stateless)
            .csrf(csrf -> csrf.disable())

            // Stateless sessions (JWT)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/login", "/register", "/auth/**",
                        "/css/**", "/js/**", "/image/**", "/images/**", "/uploads/**", "/webjars/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                // Let controller handle redirecting unauthenticated users so they see the login page
                .requestMatchers(HttpMethod.POST, "/rooms/*/book").permitAll()
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            // Logout handler: also works if controller mapping is bypassed by filter
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("jwt")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/rooms")
            )

            // Connect DB-based provider
            .authenticationProvider(daoAuthProvider());

        // Add our JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Authentication Manager bean (used in login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
