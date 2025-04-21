package com.aplikacjazespolowa.BESTTECH.controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @PostConstruct
    @Transactional
    public void DataLoader() {
        if (kategoriaRepository.count() == 0) {
            Kategoria laptopy = new Kategoria("Laptopy", "Laptopy do pracy i gier");
            Kategoria smartphony = new Kategoria("Smartphony", "Nowoczesne i biznesowe");
            Kategoria akcesoria = new Kategoria("Akcesoria", "Myszki, klawiatury, słuchawki");

            kategoriaRepository.saveAll(List.of(laptopy, smartphony, akcesoria));

            produktRepository.saveAll(List.of(
                    new Produkt("MacBook Pro", "Laptop Apple M2", 9999.99f, 10, "Apple", new Date(), laptopy),
                    new Produkt("Dell XPS 15", "Laptop dla profesjonalistów", 7999.99f, 5, "Dell", new Date(), laptopy),
                    new Produkt("iPhone 16", "Najnowszy iPhone", 5499.99f, 15, "Apple", new Date(), smartphony),
                    new Produkt("Samsung Galaxy S24", "Smartfon Samsunga", 4499.99f, 20, "Samsung", new Date(), smartphony),
                    new Produkt("Logitech MX Master", "Myszka bezprzewodowa", 399.99f, 25, "Logitech", new Date(), akcesoria)

            ));
        }
    }


    @GetMapping
    public String mainPage() {
        return "products/mainpage";
    }


    // poniżej dla admina
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


    //problem z usuwaniem kategorii, gdy istnieje produkt z daną kategorią !
    @PostMapping("/deleteproduct")
    public String usunProdukt(@RequestParam Integer id) {
        produktRepository.deleteById(id);
        return "redirect:/products/showproducts";
    }

    @GetMapping("/editproduct/{id}")
    public String pokazFormularzEdycjiProduktu(@PathVariable Integer id, Model model) {
        Produkt produkt = produktRepository.findById(id).orElse(null);
        if (produkt == null) {
            model.addAttribute("error", "Produkt o ID " + id + " nie został znaleziony");
            return "redirect:/showproducts";
        }
        List<Kategoria> kategorie = kategoriaRepository.findAll();
        model.addAttribute("produkt", produkt);
        model.addAttribute("kategorie", kategorie);
        return "products/editProduct";
    }

    @PostMapping("/editproduct")
    public String edytujProdukt(@RequestParam Integer id, @RequestParam String nazwa, @RequestParam String opis,
                                @RequestParam Float cena, @RequestParam String marka,
                                @RequestParam Integer kategoriaID, @RequestParam Integer stanMagazynowy,
                                Model model) {

        Produkt produkt = produktRepository.findById(id).orElse(null);
        if (produkt == null) {
            model.addAttribute("error", "Produkt o ID " + id + " nie został znaleziony.");
            return "products/showproducts";
        }

        Kategoria kategoria = kategoriaRepository.findById(kategoriaID).orElse(null);
        if (kategoria == null) {
            model.addAttribute("error", "Kategoria o ID " + kategoriaID + " nie została znaleziona.");
            return "products/editProduct";
        }
        produkt.setNazwa(nazwa);
        produkt.setOpis(opis);
        produkt.setCena(cena);
        produkt.setMarka(marka);
        produkt.setKategoria(kategoria);
        produkt.setStanMagazynowy(stanMagazynowy);
        produktRepository.save(produkt);

        return "redirect:/products/showproducts";
    }


    @GetMapping("/showcategories")
        public String pokazKategorie (@RequestParam(required = false) String search, Model model){
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
        public String usunKategorie (@RequestParam Integer id){
            kategoriaRepository.deleteById(id);
            return "redirect:/products/showcategories";
        }


        @GetMapping("/editcategory/{id}")
        public String pokazFormularzEdycji (@PathVariable Integer id, Model model){
            Kategoria kategoria = kategoriaRepository.findById(id).orElse(null);
            if (kategoria == null) {
                return "redirect:/showcategories";
            }
            model.addAttribute("kategoria", kategoria);
            return "products/editCategory";
        }
        @PostMapping("/editcategory")
        public String edytujKategorie (@RequestParam Integer id, @RequestParam String nazwa, @RequestParam String opis){
            Kategoria kategoria = kategoriaRepository.findById(id).orElse(null);
            if (kategoria != null) {
                kategoria.setNazwa(nazwa);
                kategoria.setOpis(opis);
                kategoriaRepository.save(kategoria);
            }
            return "redirect:/products/showcategories";
        }

        @GetMapping("/addcategory")
        public String dodajKategorie () {
            return "products/addCategory";
        }

        @PostMapping("/addcategory")
        public String dodajKategorie (@RequestParam String nazwa, @RequestParam String opis, Model model){
            Kategoria kategoria = new Kategoria();
            kategoria.setNazwa(nazwa);
            kategoria.setOpis(opis);
            kategoriaRepository.save(kategoria);
            model.addAttribute("message", "Kategoria dodana pomyślnie!");
            return "products/addCategory";
        }


    }

