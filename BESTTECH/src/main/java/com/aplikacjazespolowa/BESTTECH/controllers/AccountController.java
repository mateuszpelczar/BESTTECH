package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/konto")

public class AccountController {


    private final DBUserRepository userRepo;
    private final DBRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ZamowienieRepository zamowienieRepository;

    public AccountController(DBUserRepository userRepo, DBRoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/rejestracja")
    public String showRegisterForm() {
        return "konto/registerform";
    }


    @PostMapping("/rejestracja")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String imie,
                           @RequestParam String nazwisko,
                           @RequestParam String telefon,
                           Model model) {

        DBUser user = new DBUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setImie(imie);
        user.setNazwisko(nazwisko);
        user.setTelefon(telefon);

        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Użytkownik z takim e-mailem już istnieje.");
            return "konto/registerform";
        }


        DBRole clientRole = roleRepo.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Brak roli CLIENT w bazie"));

        user.getRoles().add(clientRole);
        userRepo.save(user);


        return "redirect:/konto/logowanie";
    }


    @GetMapping("/logowanie")
    public String logowanie() {
        return "konto/loginform";
    }


    @GetMapping("/zamowienia")
    public String showZamowieniaKlienta(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego użytkownika"));

        List<Zamowienie> zamowienia = zamowienieRepository.findByKlient_id(user.getId());

        List<String> statusOrder = Arrays.asList("w realizacji", "w drodze", "dostarczone");

        List<Zamowienie> posortowaneZamowienia = zamowienia.stream()
                .sorted(Comparator.comparingInt(z -> statusOrder.indexOf(z.getStatus())))
                .collect(Collectors.toList());

        model.addAttribute("zamowienia", posortowaneZamowienia);

        return "/konto/zamowienia";
    }

    @GetMapping("/zmien-haslo")
    public String showChangePasswordForm() {
        return "konto/changepassword";
    }

    @PostMapping("/zmien-haslo")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model
                                 ) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Nowe hasła nie są takie same.");
            return "konto/changepassword";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Aktualne hasło jest nieprawidłowe.");
            return "konto/changepassword";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        model.addAttribute("success", "Hasło zostało zmienione pomyślnie.");
        return "konto/changepassword";
    }






}

