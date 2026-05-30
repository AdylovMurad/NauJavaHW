package ru.murad.NauJava.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.murad.NauJava.entity.UserRole;

@Configuration
@EnableWebSecurity
@EnableAsync
public class SpringSecurityConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/registration", "/login", "/logout", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasAuthority(ru.murad.NauJava.entity.UserRole.ROLE_ADMIN.name())
                        .requestMatchers("/delete/**").hasAuthority(ru.murad.NauJava.entity.UserRole.ROLE_ADMIN.name())
                        .requestMatchers("/report/**").hasAuthority(ru.murad.NauJava.entity.UserRole.ROLE_ADMIN.name())
                    .requestMatchers("/admin/**").hasAuthority(UserRole.ROLE_ADMIN.name())
                    .requestMatchers("/api/users/**").hasAuthority(UserRole.ROLE_ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/books/**", "/api/authors/**", "/api/genres/**")
                    .hasAuthority(UserRole.ROLE_ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/books/**", "/api/authors/**", "/api/genres/**")
                    .hasAuthority(UserRole.ROLE_ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/books/**", "/api/authors/**", "/api/genres/**")
                    .hasAuthority(UserRole.ROLE_ADMIN.name())
                        .requestMatchers("/api/loans/admin/**").hasAuthority(UserRole.ROLE_ADMIN.name())
                        .requestMatchers("/api/books/admin").hasAuthority(UserRole.ROLE_ADMIN.name())

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(org.springframework.security.config.annotation.web.configurers.LogoutConfigurer::permitAll);

        return http.build();
    }
}