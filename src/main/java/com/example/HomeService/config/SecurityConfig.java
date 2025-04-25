package com.example.HomeService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Security configuration class for setting up Spring Security with JWT, CORS, and stateless session management.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final jwtFilter jwtFilter;

    /**
     * Constructor for injecting dependencies.
     *
     * @param userDetailsService service to load user-specific data
     * @param jwtFilter custom JWT filter to validate tokens
     */
    public SecurityConfig(UserDetailsService userDetailsService, jwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the Spring Security filter chain.
     * - Enables CORS
     * - Disables CSRF
     * - Allows unauthenticated access to /auth/**, /api/**, /payment/**
     * - Secures all other endpoints
     * - Registers JWT filter before username/password filter
     * - Enables stateless session policy
     *
     * @param http HttpSecurity instance to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS with config
                .csrf(csrf -> csrf.disable()) // Disable CSRF (suitable for token-based APIs)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**", "/api/**", "/payment/**").permitAll() // Public endpoints
                        .anyRequest().authenticated() // All other requests require auth
                )
                .authenticationProvider(authenticationProvider()) // Custom authentication provider
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before auth
                .build();
    }

    /**
     * Configures the authentication provider using a DAO-based approach.
     * - Uses BCrypt for password hashing
     * - Loads user data via UserDetailsService
     *
     * @return configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12)); // Strong password encoding
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * Provides an AuthenticationManager bean from AuthenticationConfiguration.
     *
     * @param config injected AuthenticationConfiguration
     * @return AuthenticationManager instance
     * @throws Exception if not available
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a BCryptPasswordEncoder bean with strength 12.
     *
     * @return configured password encoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures CORS settings to allow frontend communication.
     * - Allows origin: http://localhost:5173
     * - Allows common HTTP methods and headers
     * - Supports credentials like cookies and headers
     *
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173")); // Allowed frontend URL
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Methods allowed
        config.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
        config.setExposedHeaders(Collections.singletonList("*")); // Expose all headers to frontend

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS settings to all paths
        return source;
    }
}
