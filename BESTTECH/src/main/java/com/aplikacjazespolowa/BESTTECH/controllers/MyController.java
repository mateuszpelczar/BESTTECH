package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.Kategoria;
import com.aplikacjazespolowa.BESTTECH.models.KategoriaRepository;
import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class MyController {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    public MyController(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository) {
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;
    }

    @GetMapping("/")
    public String showStronaGlowna(Model model){
        model.addAttribute("message", "label");
        return "index";
    }

    // dla klienta
    @GetMapping("/kategoria")
    public String wyswietlKategorie(Model model) {
        List<Kategoria> kategorie;
        kategorie = kategoriaRepository.findAll();

        model.addAttribute("kategorie", kategorie);
        return "kategoria";
    }

    @GetMapping("/kategoria/{nazwa}")
    public String pokazKategorie(@PathVariable String nazwa, Model model) {

        List<Produkt> produkty = produktRepository.findByKategoria_Nazwa(nazwa);

        model.addAttribute("nazwaKategorii", nazwa);
        model.addAttribute("produkty", produkty);

        return "kategoria-produkty";
    }

    @GetMapping("/product")
    public String pokazProdukt(@RequestParam("id") Integer id,
                               @RequestParam("nazwa") String nazwa,
                               Model model) {

        Optional<Produkt> produktOptional = produktRepository.findById(id);

        if (produktOptional.isEmpty()) {
            // Można dodać stronę z błędem lub przekierować gdzieś
            return "redirect:/kategoria";
        }

        Produkt produkt = produktOptional.get();


        model.addAttribute("produkt", produkt);

        return "products/product-szczegoly";
    }

}
