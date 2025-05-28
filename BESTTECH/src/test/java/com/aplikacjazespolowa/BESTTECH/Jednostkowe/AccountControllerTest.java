package com.aplikacjazespolowa.BESTTECH.Jednostkowe;

import com.aplikacjazespolowa.BESTTECH.controllers.AccountController;
import com.aplikacjazespolowa.BESTTECH.models.DBRole;
import com.aplikacjazespolowa.BESTTECH.models.DBRoleRepository;
import com.aplikacjazespolowa.BESTTECH.models.DBUserRepository;
import com.aplikacjazespolowa.BESTTECH.models.ZamowienieRepository;
import com.aplikacjazespolowa.BESTTECH.security.SecurityConfig;
import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DBUserRepository userRepository;

    @MockBean
    private DBRoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ZamowienieRepository zamowienieRepository;

    @MockBean
    private RegisterService registerService;

    @Test
    void testRejestracjaUzytkownika() throws Exception {
        DBRole clientRole=new DBRole();
        clientRole.setName("CLIENT");


        // Symulacja przypadku, gdy użytkownik nie istnieje w bazie
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));

        mockMvc.perform(post("/konto/rejestracja")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("imie", "Jan")
                        .param("nazwisko", "Kowalski")
                        .param("telefon", "123456789")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isFound()); // Oczekujemy przekierowania do logowania
    }

    @Test
    void testRejestracjaUzytkownikaKtoryJuzIstnieje() throws Exception {
        DBRole clientRole= new DBRole();
        clientRole.setName("CLIENT");
        // Symulacja przypadku, gdy użytkownik już istnieje
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));

        mockMvc.perform(post("/konto/rejestracja")
                        .param("email", "existing@example.com")
                        .param("password", "password123")
                        .param("imie", "Jan")
                        .param("nazwisko", "Kowalski")
                        .param("telefon", "123456789")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()); // Powinno zwrócić formularz rejestracji z błędem
    }
}