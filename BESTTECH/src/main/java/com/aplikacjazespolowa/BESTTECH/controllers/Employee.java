package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.KategoriaRepository;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Kontroler przeznaczony dla pracownika, umożliwiający przegląd stanu magazynowego produktów.
 *
 * Wyświetla listę wszystkich produktów wraz z ich kategoriami.
 */
@Controller
@RequestMapping("/employee")
public class Employee {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    /**
     * Konstruktor wstrzykujący zależności do repozytoriów produktów i kategorii.
     *
     * @param produktRepository   repozytorium produktów
     * @param kategoriaRepository repozytorium kategorii
     */

    public Employee(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository){
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;
    }
    /**
     * Wyświetla widok inwentarza (stanu magazynowego) dla pracownika.
     *
     * Dodaje do modelu listę wszystkich produktów oraz listę kategorii.
     *
     * @param model model MVC do przekazania danych do widoku
     * @return widok inventory w katalogu employee
     */

    @GetMapping("/inventory")
    public String showStanMagazynowy(Model model){

        model.addAttribute("produkty", produktRepository.findAll());
        model.addAttribute("kategorie", kategoriaRepository.findAll());
        return "employee/inventory";
    }


}

