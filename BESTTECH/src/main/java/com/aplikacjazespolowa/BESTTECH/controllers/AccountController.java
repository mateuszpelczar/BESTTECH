package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.DBRole;
import com.aplikacjazespolowa.BESTTECH.models.DBRoleRepository;
import com.aplikacjazespolowa.BESTTECH.models.DBUser;
import com.aplikacjazespolowa.BESTTECH.models.DBUserRepository;
import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/konto")

public class AccountController {


    private final DBUserRepository userRepo;
    private final DBRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

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
    public String register(@RequestParam String email, @RequestParam String password, Model model) {

        DBUser user = new DBUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

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




}

