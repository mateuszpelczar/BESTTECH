package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/konto")
public class AccountController {

    private final RegisterService registerService;

    public AccountController(RegisterService registerService) {
        this.registerService = registerService;
    }




    @GetMapping("/rejestracja")
    public String showRegisterForm() {
        return "konto/registerform";
    }


    @PostMapping("/rejestracja")
    public String register(@RequestParam String email, @RequestParam String password) {
        registerService.register(email, password);
        return "redirect:/konto/logowanie";
    }


    @GetMapping("/logowanie")
    public String logowanie() {
        return "konto/loginform";
    }


}

