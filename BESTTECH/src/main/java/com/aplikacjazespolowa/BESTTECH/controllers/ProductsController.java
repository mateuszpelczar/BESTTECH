package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.dto.RaportSprzedazyDTO;
import com.aplikacjazespolowa.BESTTECH.models.*;
import com.aplikacjazespolowa.BESTTECH.services.ProduktService;
import com.aplikacjazespolowa.BESTTECH.services.ZamowienieService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

/**
 * Kontroler zarządzający produktami i kategoriami w systemie sklepu.
 * Umożliwia administratorowi:
 * <ul>
 *   <li>przeglądanie, dodawanie, edytowanie i usuwanie produktów oraz kategorii,</li>
 *   <li>generowanie raportów sprzedaży w wybranym przedziale czasowym,</li>
 *   <li>rejestrowanie działań w systemie logów.</li>
 * </ul>
 */
@Controller
@RequestMapping("/products")
public class ProductsController {
    private final ProduktRepository produktRepository;
    private final KategoriaRepository kategoriaRepository;

    @Autowired
    ZamowienieService zamowienieService;
    @Autowired
    LogsRepository logsRepository;
    @Autowired
    private HttpServletRequest request;

    /**
     * Inicjalizuje kontroler produktami i kategoriami.
     *
     * @param produktRepository repozytorium produktów
     * @param kategoriaRepository repozytorium kategorii
     */

    public ProductsController(ProduktRepository produktRepository, KategoriaRepository kategoriaRepository) {
        this.produktRepository = produktRepository;
        this.kategoriaRepository = kategoriaRepository;

    }

    /**
     * Metoda wykonywana po utworzeniu beana. Wypełnia bazę danych przykładowymi kategoriami i produktami, jeśli jest pusta.
     */

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
    /**
     * Strona główna sekcji produktów.
     *
     * @return widok strony głównej produktów ("products/mainpage")
     */

    @GetMapping
    public String mainPage() {
        return "products/mainpage";
    }



    /**
     * Wyświetla listę produktów z opcjonalnym filtrem nazwy lub kategorii.
     *
     * @param search     opcjonalny fragment nazwy produktu
     * @param categoryId opcjonalny identyfikator kategorii
     * @param model      model danych do widoku
     * @return widok listy produktów ("products/showProducts")
     */

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


    /**
     * Formularz dodawania nowego produktu.
     *
     * @param model model danych do widoku
     * @return widok formularza dodawania produktu ("products/addProduct")
     */

    @GetMapping("/addproduct")
    public String dodajProdukt(Model model) {
        model.addAttribute("kategorie", kategoriaRepository.findAll()); // Przekazywanie listy kategorii
        return "products/addProduct";
    }

    /**
     * Obsługuje dodanie nowego produktu do systemu.
     *
     * @param nazwa         nazwa produktu
     * @param opis          opis produktu
     * @param cena          cena produktu
     * @param stanMagazynowy ilość dostępnych sztuk
     * @param marka         marka produktu
     * @param kategoriaID   ID wybranej kategorii
     * @param zdjecieUrl    adres URL zdjęcia
     * @param model         model danych
     * @param redirectAttributes atrybuty przekazywane po przekierowaniu
     * @param request       obiekt żądania HTTP
     * @return przekierowanie na listę produktów
     */

    @PostMapping("/addproduct")
    public String dodajProdukt(@RequestParam String nazwa,
                               @RequestParam String opis,
                               @RequestParam float cena,
                               @RequestParam Integer stanMagazynowy,
                               @RequestParam String marka,
                               @RequestParam Integer kategoriaID,
                               @RequestParam String zdjecieUrl,
                               Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Optional<Kategoria> kOpt = kategoriaRepository.findById(kategoriaID);
        if (kOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kategoria o ID " + kategoriaID + " nie zostala znaleziona");
            return "redirect:/products/addproduct/";
        }
        Kategoria kategoria = kOpt.get();

        Produkt produkt = new Produkt();
        produkt.setNazwa(nazwa);
        produkt.setOpis(opis);
        produkt.setCena(cena);
        produkt.setStanMagazynowy(stanMagazynowy);
        produkt.setMarka(marka);
        produkt.setDataDodania(new Date());
        produkt.setKategoria(kategoria);
        produkt.setZdjecieUrl(zdjecieUrl);

        String currentUser = request.getUserPrincipal().getName();
        logsRepository.save(new LogsSystem("Dodano nowy produkt: " + nazwa, currentUser, "INFO"));

        produktRepository.save(produkt);

        redirectAttributes.addFlashAttribute("message", "Produkt zostal dodany.");
        return "redirect:/products/showproducts";
    }

    /**
     * Usuwa produkt o podanym ID.
     *
     * @param id                identyfikator produktu
     * @param request           żądanie HTTP (do pobrania aktualnego użytkownika)
     * @param redirectAttributes atrybuty do przekazania po przekierowaniu
     * @return przekierowanie na listę produktów
     */

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

    /**
     * Formularz edycji produktu.
     *
     * @param id    identyfikator edytowanego produktu
     * @param model model danych do widoku
     * @return widok formularza edycji ("products/editProduct")
     */

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

    /**
     * Aktualizuje dane istniejącego produktu.
     *
     * @param id              identyfikator produktu
     * @param nazwa           nowa nazwa
     * @param opis            nowy opis
     * @param cena            nowa cena
     * @param marka           nowa marka
     * @param kategoriaID     nowy ID kategorii
     * @param stanMagazynowy  nowy stan magazynowy
     * @param zdjecieUrl      nowy URL zdjęcia
     * @param model           model danych
     * @param request         żądanie HTTP
     * @param redirectAttributes atrybuty po przekierowaniu
     * @return przekierowanie na listę produktów
     */

    @PostMapping("/editproduct")
    public String edytujProdukt(@RequestParam Integer id,
                                @RequestParam String nazwa,
                                @RequestParam String opis,
                                @RequestParam Float cena,
                                @RequestParam String marka,
                                @RequestParam Integer kategoriaID,
                                @RequestParam Integer stanMagazynowy,
                                @RequestParam String zdjecieUrl,
                                Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Optional<Produkt> opt = produktRepository.findById(id);
        if(opt.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Produkt o ID " + id + " nie został znaleziony");
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
        produkt.setZdjecieUrl(zdjecieUrl);
        produktRepository.save(produkt);

        redirectAttributes.addFlashAttribute("message","Produkt został zaktualizowany");
        return "redirect:/products/showproducts";
    }

    /**
     * Wyświetla listę kategorii z możliwością wyszukiwania.
     *
     * @param search opcjonalny fragment nazwy kategorii
     * @param model  model danych do widoku
     * @return widok listy kategorii ("products/showCategories")
     */

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

    /**
     * Usuwa kategorię o podanym ID.
     *
     * @param id                identyfikator kategorii
     * @param request           żądanie HTTP
     * @param redirectAttributes atrybuty po przekierowaniu
     * @return przekierowanie na listę kategorii
     */

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

    /**
     * Formularz edycji kategorii.
     *
     * @param id    identyfikator kategorii
     * @param model model danych
     * @return widok formularza edycji kategorii ("products/editCategory")
     */

    @GetMapping("/editcategory/{id}")
    public String pokazFormularzEdycji (@PathVariable Integer id, Model model){
        Kategoria kategoria = kategoriaRepository.findById(id).orElse(null);
        if (kategoria == null) {
            return "redirect:/showcategories";
        }
        model.addAttribute("kategoria", kategoria);
        return "products/editCategory";
    }

    /**
     * Zapisuje zmiany w edytowanej kategorii.
     *
     * @param id                identyfikator kategorii
     * @param nazwa             nowa nazwa
     * @param opis              nowy opis
     * @param request           żądanie HTTP
     * @param redirectAttributes atrybuty po przekierowaniu
     * @return przekierowanie na listę kategorii
     */
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

    /**
     * Formularz dodawania nowej kategorii.
     *
     * @return widok formularza dodawania kategorii ("products/addCategory")
     */

    @GetMapping("/addcategory")
    public String dodajKategorie () {
        return "products/addCategory";
    }

    /**
     * Obsługuje dodanie nowej kategorii.
     *
     * @param nazwa             nazwa kategorii
     * @param opis              opis kategorii
     * @param model             model danych
     * @param request           żądanie HTTP
     * @param redirectAttributes atrybuty po przekierowaniu
     * @return przekierowanie na listę kategorii
     */

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
        return "products/showcategories";
    }

    /**
     * Konwertuje tablicę bajtów na tekst zakodowany w Base64.
     *
     * @param bytes dane binarne
     * @return tekst zakodowany w Base64 lub null, jeśli dane są puste
     */
    public String convertToBase64(byte[] bytes){
        return  bytes != null ? Base64.getEncoder().encodeToString(bytes) : null;
    }

    /**
     * Generuje raport sprzedaży w podanym zakresie dat.
     *
     * @param od      data początkowa raportu
     * @param doDaty  data końcowa raportu
     * @param model   model danych do widoku
     * @return widok raportu sprzedaży ("products/raportSprzedazy")
     */

    @GetMapping("/raportSprzedazy")
    public String raportSprzedazy(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate od,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate doDaty,
                                  Model model) {
        List<RaportSprzedazyDTO> raport = null;

        if(od !=null && doDaty !=null) {
            raport = zamowienieService.generujRaportSprzedazy(od, doDaty);
            model.addAttribute("odDo", "Od " + od + " do " + doDaty);

        }

        model.addAttribute("raportSprzedazy",raport);
        model.addAttribute("od",od);
        model.addAttribute("doDaty",doDaty);
        return "products/raportSprzedazy";

    }
    /**
     * Wyszukuje produkty na podstawie fragmentu nazwy.
     *
     * @param query  fragment nazwy produktu do wyszukania
     * @param model  model danych do widoku
     * @return widok wyników wyszukiwania ("products/searchResults")
     */
    @GetMapping("/search")
    public String wyszukajProdukty(@RequestParam String query, Model model) {
        List<Produkt> produkty = produktRepository.findByNazwaContainingIgnoreCase(query);
        model.addAttribute("produkty", produkty);
        model.addAttribute("query", query);
        return "products/searchResults";
    }

//     @GetMapping("/details/{id}")
// public String getProductDetails(@PathVariable Integer id, Model model) {
//     Produkt produkt = produktRepository.findById(id)
//             .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o ID: " + id));
//     model.addAttribute("produkt", produkt);
//     return "products/productDetails";
// }
}
