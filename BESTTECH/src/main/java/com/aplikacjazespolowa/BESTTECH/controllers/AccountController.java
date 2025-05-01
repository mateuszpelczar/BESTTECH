package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


}

