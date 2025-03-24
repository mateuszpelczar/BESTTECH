package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @GetMapping("/")
    public String showStronaGlowna(Model model){
        model.addAttribute("message", "TUTAJ COS KIEDYS BEDZIE");
        return "mainpage";
    }

}
