package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import com.aplikacjazespolowa.BESTTECH.models.Kategoria;
import com.aplikacjazespolowa.BESTTECH.models.KategoriaRepository;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    public ProductsController(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository) {
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;
    }

    @GetMapping
    public String mainPage() {
        return "products/mainpage";
    }

    @GetMapping("/showproducts")
    public String pokazProdukty(@RequestParam(required = false) String search,
                                @RequestParam(required = false) Integer categoryId,
                                Model model) {

        List<Produkt> produkty;
        if (search != null && !search.isEmpty()) {
            produkty = produktRepository.findByNazwaContainingIgnoreCase(search);
        } else if (categoryId != null) {
            produkty = produktRepository.findByKategoria_KategoriaID(categoryId);
        } else {
            produkty = produktRepository.findAll();
        }
        List<Kategoria> kategorie = kategoriaRepository.findAll();

        model.addAttribute("produkty", produkty);
        model.addAttribute("kategorie", kategorie);
        return "products/showProducts";
    }

    @GetMapping("/addproduct")
    public String dodajProdukt(Model model) {
        model.addAttribute("kategorie", kategoriaRepository.findAll()); // Przekazywanie listy kategorii
        return "products/addProduct";
    }

    @PostMapping("/addproduct")
    public String dodajProdukt(@RequestParam String nazwa,
                               @RequestParam String opis,
                               @RequestParam float cena,
                               @RequestParam Integer stanMagazynowy,
                               @RequestParam String marka,
                               @RequestParam Integer kategoriaID,
                               Model model) {
        Kategoria kategoria = kategoriaRepository.findById(kategoriaID).orElse(null);
        if (kategoria != null) {
            Produkt produkt = new Produkt();
            produkt.setNazwa(nazwa);
            produkt.setOpis(opis);
            produkt.setCena(cena);
            produkt.setStanMagazynowy(stanMagazynowy);
            produkt.setMarka(marka);
            produkt.setDataDodania(new Date()); // Ustawiamy bieżącą datę
            produkt.setKategoria(kategoria);

            produktRepository.save(produkt); // Zapisujemy produkt do bazy
        }

        return "redirect:/products/showproducts"; // Przekierowanie do strony z produktami
    }

    @GetMapping("/showcategories")
    public String pokazKategorie(@RequestParam(required = false) String search, Model model) {
        List<Kategoria> kategorie;
        if (search != null && !search.isEmpty()) {
            kategorie = kategoriaRepository.findByNazwaContainingIgnoreCase(search);
        } else {
            kategorie = kategoriaRepository.findAll();
        }
        model.addAttribute("kategorie", kategorie);
        return "products/showCategories";
    }

    //problem z usuwaniem kategorii, gdy istnieje produkt z daną kategorią !
    @PostMapping("/deletecategory")
    public String usunKategorie(@RequestParam Integer id) {
        kategoriaRepository.deleteById(id);
        return "redirect:/products/showcategories";
    }

    @GetMapping("/editcategory/{id}")
    public String pokazFormularzEdycji(@PathVariable Integer id, Model model) {
        Kategoria kategoria = kategoriaRepository.findById(id).orElse(null);
        if (kategoria == null) {
            return "redirect:/showcategories";
        }
        model.addAttribute("kategoria", kategoria);
        return "products/editCategory";
    }
    @PostMapping("/editcategory")
    public String edytujKategorie(@RequestParam Integer id, @RequestParam String nazwa, @RequestParam String opis) {
        Kategoria kategoria = kategoriaRepository.findById(id).orElse(null);
        if (kategoria != null) {
            kategoria.setNazwa(nazwa);
            kategoria.setOpis(opis);
            kategoriaRepository.save(kategoria);
        }
        return "redirect:/products/showcategories";
    }

    @GetMapping("/addcategory")
    public String dodajKategorie() {
        return "products/addCategory";
    }

    @PostMapping("/addcategory")
    public String dodajKategorie(@RequestParam String nazwa, @RequestParam String opis, Model model) {
        Kategoria kategoria = new Kategoria();
        kategoria.setNazwa(nazwa);
        kategoria.setOpis(opis);
        kategoriaRepository.save(kategoria);
        model.addAttribute("message", "Kategoria dodana pomyślnie!");
        return "products/addCategory";
    }


}
