package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/konto")
public class AccountController {


    @GetMapping("/rejestracja")
    public String rejestracja(Model model)
    {
        return "konto/registerform";
    }

    @GetMapping("/logowanie")
    public String logowanie(Model model)
    {
        return "konto/loginform";
    }

}
