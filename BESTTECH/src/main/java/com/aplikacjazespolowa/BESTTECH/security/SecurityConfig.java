package com.aplikacjazespolowa.BESTTECH.security;

import com.aplikacjazespolowa.BESTTECH.models.DBRole;
import com.aplikacjazespolowa.BESTTECH.models.DBUser;
import com.aplikacjazespolowa.BESTTECH.models.DBUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
/**
 * Konfiguracja zabezpieczeń Spring Security dla aplikacji.
 */
@Configuration
public class SecurityConfig {
    /**
     * Główna konfiguracja łańcucha filtrów bezpieczeństwa.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // WYŁĄCZENIE CSRF
                .authorizeHttpRequests(authorize -> authorize

                        // publiczne endpointy
                        .requestMatchers("/", "/konto/rejestracja", "/konto/logowanie", "/resources/**", "/css/**", "/js/**", "/koszyk", "/zdjecia/**", "/scripts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product", "/kategoria/**", "/products/search").permitAll()
                        .requestMatchers(HttpMethod.GET,"/konto/rejestracja").permitAll()
                        .requestMatchers(HttpMethod.POST,"/konto/rejestracja").permitAll()

                        // dostęp dla admina i pracownika
                        .requestMatchers(HttpMethod.GET, "/products", "/products/showproducts", "/products/editproduct/{id}", "/products/addproduct", "/products/addcategory", "/products/editcategory/{id}", "/employee/inventory", "/products/showcategories").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/products", "/products/editproduct", "/products/deleteproduct", "/products/addproduct", "/products/addcategory", "/products/editcategory", "/products/deletecategory").hasAnyRole("ADMIN", "EMPLOYEE")

                        // dostęp tylko dla admina
                        .requestMatchers(HttpMethod.GET, "/admin", "/admin/manageusers", "/admin/manageusers/changerole", "/admin/manageemployess", "/admin/logs", "/admin/deleteemployee").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin", "/admin/manageusers/changerole/save", "/admin/deleteemployee", "/admin/deleteemployee").hasRole("ADMIN")

                        // dostęp do zarządzania opiniami tylko dla ADMIN i EMPLOYEE
                        .requestMatchers(HttpMethod.GET, "/opinie/zarzadzaj_opiniami").hasAnyRole("ADMIN", "EMPLOYEE")

                        // dostęp do koszyka dla niezalogowanych użytkowników
                        .requestMatchers(HttpMethod.GET, "/koszyk").permitAll()

                        // dla zalogowanych użytkowników
                        .requestMatchers(HttpMethod.POST, "/koszyk/**").authenticated()

                        // domyślnie reszta wymaga logowania
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
    /**
     * Bean odpowiedzialny za szyfrowanie haseł.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Bean UserDetailsService, który ładuje użytkownika z bazy na podstawie adresu e-mail.
     */
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
