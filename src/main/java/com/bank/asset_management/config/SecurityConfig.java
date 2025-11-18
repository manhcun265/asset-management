package com.bank.asset_management.config;

import com.bank.asset_management.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtServiceImpl jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("[SECURITY] Configuring SecurityFilterChain");

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userDetailsService, jwtService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("[SECURITY] Creating DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final UserDetailsService userDetailsService;
        private final JwtServiceImpl jwtService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String path = request.getRequestURI();
            String method = request.getMethod();
            String authHeader = request.getHeader("Authorization");

            log.debug("[JWT-FILTER] {} {} - Auth header: {}", method, path, authHeader != null ? "present" : "null");

            // Skip public endpoints
            if (path.startsWith("/api/auth/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Skip if no auth header or not Bearer
            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                log.warn("[JWT-FILTER] Missing or invalid Authorization header for {} {}", method, path);
                filterChain.doFilter(request, response);
                return;
            }

            try {
                // Extract token (after "Bearer ")
                String token = authHeader.substring(7).trim();
                log.debug("[JWT-FILTER] Token extracted: {}...", token.length() > 20 ? token.substring(0, 20) : token);

                // Check if token is blacklisted
                if (jwtService.isTokenInvalidated(token)) {
                    log.warn("[JWT-FILTER] Token is blacklisted");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Check if token is expired
                if (jwtService.isTokenExpired(token)) {
                    log.warn("[JWT-FILTER] Token is expired");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Extract username and set authentication
                String username = jwtService.extractUsername(token);
                log.debug("[JWT-FILTER] Extracted username: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("[JWT-FILTER] Authenticated user: {} with {} authorities",
                            username, userDetails.getAuthorities() != null ? userDetails.getAuthorities().size() : 0);
                    }
                }
            } catch (Exception e) {
                log.error("[JWT-FILTER] Error processing JWT: {}", e.getMessage(), e);
            }

            filterChain.doFilter(request, response);
        }
    }
}

