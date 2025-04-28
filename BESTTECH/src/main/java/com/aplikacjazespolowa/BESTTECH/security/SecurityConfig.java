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

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable()) // WYŁĄCZENIE CSRF
                .authorizeHttpRequests(authorize -> authorize

                        //publiczne endpointy
                        .requestMatchers("/","/konto/rejestracja", "/konto/logowanie", "/resources/**", "/css/**", "/js/**","/koszyk").permitAll()
                        .requestMatchers(HttpMethod.GET,"/product","/kategoria/**").permitAll()

                        //dostep dla admina i pracownika
                        .requestMatchers(HttpMethod.GET,"/products","/products/showproducts", "/products/editproduct/{id}","/products/addproduct", "/products/addcategory","/products/editcategory/{id}","/employee/inventory","/products/showcategories").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/products","/products/editproduct","/products/deleteproduct","/products/addproduct", "/products/addcategory","/products/editcategory","/products/deletecategory").hasAnyRole("ADMIN", "EMPLOYEE")

                        //dostep tylko dla admina
                        .requestMatchers(HttpMethod.GET,"/admin","/admin/manageusers","/admin/manageusers/changerole","/admin/manageemployess","/admin/logs","/admin/deleteemployee").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/admin","/admin/manageusers/changerole/save","/admin/deleteemployee","/admin/deleteemployee").hasRole("ADMIN")

                        // dostęp do zarządzania opiniami tylko dla ADMIN i EMPLOYEE
                        .requestMatchers(HttpMethod.GET, "/opinie/zarzadzaj_opiniami").hasAnyRole("ADMIN", "EMPLOYEE")

                        //dostep do koszyka dla niezalogowanych users
                        .requestMatchers(HttpMethod.GET,"/koszyk").permitAll()

                        //dla zalogowanych users
                        .requestMatchers(HttpMethod.POST,"/koszyk/**").authenticated()

                        //domyslnie reszta wymaga logowania
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
                )



        ;

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
