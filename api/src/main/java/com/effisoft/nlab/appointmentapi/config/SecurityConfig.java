package com.effisoft.nlab.appointmentapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

    // Only log authentication errors and important security events
    private void logAuthenticationError(String message, Object... args) {
        logger.error(message, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permit OPTIONS requests for CORS preflight
                        .requestMatchers("OPTIONS", "/**").permitAll()
                        // Explicitly require specific roles for each endpoint
                        .requestMatchers("/api/nutritionists/**").hasAnyRole("ADMIN", "NUTRITIONIST")
                        .requestMatchers("/api/patients/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
                        .requestMatchers("/api/appointments/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
                        .requestMatchers("/api/package-types/**").hasAnyRole("ADMIN", "NUTRITIONIST")
                        .requestMatchers("/api/purchased-packages/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
                        .requestMatchers("/api/payment-methods/**").hasAnyRole("ADMIN", "NUTRITIONIST")
                        .requestMatchers("/api/card-payment-types/**").hasAnyRole("ADMIN", "NUTRITIONIST")
                        // Any other endpoint requires authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            if (jwt.getClaim("realm_access") == null) {
                logAuthenticationError("No realm_access claim found in JWT");
                return new ArrayList<>();
            }

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess.get("roles") == null) {
                logAuthenticationError("No roles found in realm_access");
                return new ArrayList<>();
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(roleName -> "ROLE_" + roleName.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return authorities;
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}