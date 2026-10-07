package com.hospital;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import service.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            .exceptionHandling(exception -> exception

            	    .authenticationEntryPoint((request, response, authException) -> {
            	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	        response.setContentType("application/json");
            	        response.getWriter().write(
            	            "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required or token is invalid\"}"
            	        );
            	    })

            	    .accessDeniedHandler((request, response, accessDeniedException) -> {
            	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            	        response.setContentType("application/json");
            	        response.getWriter().write(
            	            "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource\"}"
            	        );
            	    })
            	)

            .authorizeHttpRequests(auth -> auth
            		
            		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            	    // Public endpoints
            		.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            		.requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

            	    // Appointment access
            	    .requestMatchers("/api/appointments/**")
            	    .hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")

            	    // Medical records
            	    .requestMatchers("/api/medical-records/**")
            	    .hasAnyRole("ADMIN", "DOCTOR")

            	    // Prescriptions
            	    .requestMatchers(
            	        "/api/prescriptions/**",
            	        "/api/prescription-items/**"
            	    ).hasAnyRole("ADMIN", "DOCTOR", "PHARMACIST")

            	    // Patient and doctor management
            	    .requestMatchers(
            	        "/api/patients/**",
            	        "/api/doctors/**"
            	    ).hasAnyRole("ADMIN", "RECEPTIONIST")

            	    // Admission and room management
            	    .requestMatchers(
            	        "/api/admissions/**",
            	        "/api/rooms/**"
            	    ).hasAnyRole("ADMIN", "RECEPTIONIST")

            	    // Pharmacy
            	    .requestMatchers("/api/medicines/**")
            	    .hasAnyRole("ADMIN", "PHARMACIST")

            	    // Billing and payments
            	    .requestMatchers(
            	        "/api/bills/**",
            	        "/api/payments/**"
            	    ).hasAnyRole("ADMIN", "ACCOUNTANT")

            	    // Dashboard
            	    .requestMatchers("/api/dashboard/**")
            	    .hasAnyRole("ADMIN", "ACCOUNTANT")

            	    // Remaining API endpoints
            	    .requestMatchers("/api/**").hasRole("ADMIN")

            	    .anyRequest().authenticated()
            	)

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    
   

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:5173","https://hospital-management-frontend-sw03.onrender.com")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}