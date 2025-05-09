package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductsController {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    @Autowired
    LogsRepository logsRepository;
    @Autowired
    private HttpServletRequest request;

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
                               Model model,RedirectAttributes redirectAttributes, HttpServletRequest request) {

        // pobranie kategorii
        Optional<Kategoria>kOpt = kategoriaRepository.findById(kategoriaID);
        if(kOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Kategoria o ID " + kategoriaID + " nie zostala znaleziona");
            return "redirect://products/addproduct/";
        }
        Kategoria kategoria=kOpt.get();

        Produkt produkt = new Produkt();
        produkt.setNazwa(nazwa);
        produkt.setOpis(opis);
        produkt.setCena(cena);
        produkt.setStanMagazynowy(stanMagazynowy);
        produkt.setMarka(marka);
        produkt.setDataDodania(new Date()); // Ustawiamy bieżącą datę
        produkt.setKategoria(kategoria);

        //log
        String currentUser=request.getUserPrincipal().getName();
        logsRepository.save(new LogsSystem("Dodano nowy produkt: " + nazwa ,currentUser,"INFO"));

        produktRepository.save(produkt); // Zapisujemy produkt do bazy

        redirectAttributes.addFlashAttribute("message","Produkt zostal dodany.");
        return "redirect:/products/showproducts"; // Przekierowanie do strony z produktami
    }


    //problem z usuwaniem kategorii, gdy istnieje produkt z daną kategorią !
    @PostMapping("/deleteproduct")
    public String usunProdukt(@RequestParam Integer id,HttpServletRequest request, RedirectAttributes redirectAttributes) {
        //pobranie produktu
        Optional<Produkt> opt=produktRepository.findById(id);
        if(opt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Produkt o ID " + id + " nie zostal znaleziony");
            return "redirect://products/showproduct/";
        }
        Produkt produkt = opt.get();

        //zapis log o edycji produktu
        String currentUser=request.getUserPrincipal().getName();
        Integer deletedProductID=produkt.getProduktID();
        logsRepository.save(new LogsSystem("Usunieto produkt" + deletedProductID, currentUser,"WARN"));

        produktRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message","Produkt został usuniety");
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
                                Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        //znajdywanie produktu
        Optional<Produkt> opt = produktRepository.findById(id);
        if(opt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Produkt o ID " + id + "nie został znaleziony");
            return "redirect:/products/showproducts";
        }
        Produkt produkt = opt.get();

        // pobranie kategorii
        Optional<Kategoria>kOpt = kategoriaRepository.findById(kategoriaID);
        if(kOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Kategoria o ID " + kategoriaID + " nie zostala znaleziona");
            return "redirect://products/editproduct/" + id;
        }
        Kategoria kategoria=kOpt.get();


        //zapis log o edycji produktu
        String currentUser=request.getUserPrincipal().getName();
        Integer editProductID=produkt.getProduktID();
        logsRepository.save(new LogsSystem("Zmieniono produkt" + editProductID, currentUser,"INFO"));

        produkt.setNazwa(nazwa);
        produkt.setOpis(opis);
        produkt.setCena(cena);
        produkt.setMarka(marka);
        produkt.setKategoria(kategoria);
        produkt.setStanMagazynowy(stanMagazynowy);
        produktRepository.save(produkt);

        redirectAttributes.addFlashAttribute("message","Produkt został zaktualizowany");
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
    public String usunKategorie (@RequestParam Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes){
        Optional<Kategoria> kOpt=kategoriaRepository.findById(id);
        if(kOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error",
                    "Kategoria o ID " + id + " nie została znaleziona");
            return "redirect:/products/showcategories";
        }
        Kategoria kategoria=kOpt.get();

        //log
        String currentUser=request.getUserPrincipal().getName();
        String deletedCategoryName=kategoria.getNazwa();
        logsRepository.save(new LogsSystem("Usunieto kategorie " + deletedCategoryName, currentUser,"WARN"));

        kategoriaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message","Kategoria \"" + deletedCategoryName + "\" została usunieta");
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
    public String edytujKategorie (@RequestParam Integer id, @RequestParam String nazwa, @RequestParam String opis,HttpServletRequest request, RedirectAttributes redirectAttributes){

        Optional<Kategoria> kopt=kategoriaRepository.findById(id);
        if(kopt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Kategorii o id: " + id + "nie mozna edytowac");
            return "redirect:/products/showcategories";
        }
        Kategoria kategoria = kopt.get();

        //log
        String currentUser=request.getUserPrincipal().getName();
        String editCategoryName=kategoria.getNazwa();
        logsRepository.save(new LogsSystem("Zaktualizowano kategorię" + editCategoryName, currentUser,"INFO"));

        kategoria.setNazwa(nazwa);
        kategoria.setOpis(opis);
        kategoriaRepository.save(kategoria);

        redirectAttributes.addFlashAttribute("message","Kategoria zostala zaktualizowana");
        return "redirect:/products/showcategories";
    }

    @GetMapping("/addcategory")
    public String dodajKategorie () {
        return "products/addCategory";
    }

    @PostMapping("/addcategory")
    public String dodajKategorie (@RequestParam String nazwa, @RequestParam String opis, Model model,HttpServletRequest request, RedirectAttributes redirectAttributes){

        //tworzenie nowej kategorii
        Kategoria kategoria = new Kategoria();
        kategoria.setNazwa(nazwa);
        kategoria.setOpis(opis);


        //zapisanie log przed zapisem do bazy, zapisanie po raz 1
        Kategoria saved = kategoriaRepository.save(kategoria);

        //log
        String CurrentUser=request.getUserPrincipal().getName();
        String addingCategory=saved.getNazwa();
        logsRepository.save(new LogsSystem("Dodano nowa kategorie:  " + addingCategory, CurrentUser,"INFO"));


        redirectAttributes.addFlashAttribute("message","Dodano nowa kategorie: " + addingCategory);
        return "/products/showcategories";
    }



}

