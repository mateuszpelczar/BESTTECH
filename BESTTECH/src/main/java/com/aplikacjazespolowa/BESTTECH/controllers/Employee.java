package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.KategoriaRepository;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class Employee {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    public Employee(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository){
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;
    }

    //dla pracownika
    @GetMapping("/inventory")
    public String showStanMagazynowy(Model model){

        model.addAttribute("produkty", produktRepository.findAll());
        model.addAttribute("kategorie", kategoriaRepository.findAll());
        return "employee/inventory";
    }


}

