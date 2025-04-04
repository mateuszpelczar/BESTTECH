package com.aplikacjazespolowa.BESTTECH.security;

import com.aplikacjazespolowa.BESTTECH.models.DBRole;
import com.aplikacjazespolowa.BESTTECH.models.DBUser;
import com.aplikacjazespolowa.BESTTECH.models.DBUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/konto/rejestracja", "/konto/logowanie", "/resources/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/konto/logowanie")
                        .loginProcessingUrl("/konto/logowanie")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/konto/wyloguj")
                        .logoutSuccessUrl("/konto/logowanie?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DBUserRepository userRepository) {
        return username -> {
            DBUser user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

            Set<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toSet());

            return new User(user.getEmail(), user.getPassword(), authorities);
        };
    }
}
