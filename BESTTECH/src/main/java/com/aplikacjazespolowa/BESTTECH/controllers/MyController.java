package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.Kategoria;
import com.aplikacjazespolowa.BESTTECH.models.KategoriaRepository;
import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import com.aplikacjazespolowa.BESTTECH.services.ProduktService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Główny kontroler aplikacji, odpowiedzialny za wyświetlanie strony głównej,
 * listy kategorii produktów, produktów w wybranej kategorii oraz szczegółów pojedynczego produktu.
 */
@Controller
public class MyController {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;
    private final ProduktService produktService;

    /**
     * Konstruktor wstrzykujący zależności do repozytoriów i serwisu produktów.
     *
     * @param produktRepository repozytorium produktów
     * @param kategoriaRepository repozytorium kategorii
     * @param produktService serwis zarządzający logiką produktów
     */

    public MyController(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository, ProduktService produktService) {
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;
        this.produktService=produktService;
    }

    /**
     * Wyświetla stronę główną sklepu.
     *
     * Jeśli użytkownik jest zalogowany, wypisuje jego nazwę w konsoli.
     * Do modelu dodaje listę 5 losowych produktów jako polecane.
     *
     * @param model model MVC do przekazania danych do widoku
     * @param principal obiekt reprezentujący zalogowanego użytkownika (jeśli istnieje)
     * @return widok strony głównej ("index")
     */

    @GetMapping("/")
    public String showStronaGlowna(Model model, Principal principal) {
        if (principal != null) {
            System.out.println("Zalogowany: " + principal.getName());
        }

        //Pobranie 5 losowych produktow
        List<Produkt> losoweProdukty = produktService.getLosoweProdukty(5);

        model.addAttribute("polecaneProdukty",losoweProdukty);

        return "index";
    }

    /**
     * Wyświetla listę wszystkich dostępnych kategorii produktów.
     *
     * @param model model MVC do przekazania danych do widoku
     * @return widok listy kategorii ("kategoria")
     */

    // dla klienta
    @GetMapping("/kategoria")
    public String wyswietlKategorie(Model model) {
        List<Kategoria> kategorie;
        kategorie = kategoriaRepository.findAll();

        model.addAttribute("kategorie", kategorie);
        return "kategoria";
    }

    /**
     * Wyświetla produkty należące do wskazanej kategorii.
     *
     * @param nazwa nazwa kategorii, po której filtrowane są produkty
     * @param model model MVC do przekazania danych do widoku
     * @return widok produktów w kategorii ("kategoria-produkty")
     */

    @GetMapping("/kategoria/{nazwa}")
    public String pokazKategorie(@PathVariable String nazwa, Model model) {

        List<Produkt> produkty = produktRepository.findByKategoria_Nazwa(nazwa);

        model.addAttribute("nazwaKategorii", nazwa);
        model.addAttribute("produkty", produkty);

        return "kategoria-produkty";
    }

    /**
     * Wyświetla szczegóły pojedynczego produktu.
     *
     * @param id ID produktu do wyświetlenia
     * @param nazwa nazwa produktu (może służyć do SEO lub walidacji)
     * @param model model MVC do przekazania danych do widoku
     * @return widok szczegółów produktu ("products/product-szczegoly") lub przekierowanie do listy kategorii jeśli produkt nie istnieje
     */

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

        List<Produkt> losoweProdukty = produktService.getLosoweProdukty(5);
        model.addAttribute("polecaneProdukty",losoweProdukty);

        return "products/product-szczegoly";
    }

}
