package com.dhbw.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deaktiviert für APIs (wichtig!)
            .cors(cors -> cors.configure(http)) // Nutzt deine CorsConfig
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Wir nutzen JWT, keine Sessions
            .authorizeHttpRequests(auth -> auth
                // Diese Endpunkte sind für JEDEN frei zugänglich (Registrierung, Login, Swagger)
                .requestMatchers("/api/users/register", "/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Für alles andere MUSS man eingeloggt sein (Token haben)
                .anyRequest().authenticated()
            );

        return http.build();
    }
}