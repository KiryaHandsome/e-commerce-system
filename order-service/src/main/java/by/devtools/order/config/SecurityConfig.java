package by.devtools.order.config;

import by.devtools.order.controller.filter.AuthenticationJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationJwtFilter authenticationJwtFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(authenticationJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
