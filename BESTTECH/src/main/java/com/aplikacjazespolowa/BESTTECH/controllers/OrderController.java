package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Kontroler zarządzający procesem składania zamówień w sklepie internetowym.
 *
 * Umożliwia podgląd podsumowania koszyka, dodanie adresu dostawy,
 * zapisanie zamówienia wraz ze szczegółami oraz wyświetlenie potwierdzenia złożenia zamówienia.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    private final ProduktRepository produktRepository;

    private final AdresDostawyRepository adresDostawyRepository;

    private final ZamowienieRepository zamowienieRepository;
    private final SzczegolyZamowieniaRepository szczegolyZamowieniaRepository;

    private final DostawaRepository dostawaRepository;

    private final DBUserRepository dbUserRepository;

    /**
     * Konstruktor wstrzykujący wymagane repozytoria.
     */

    public OrderController(ProduktRepository produktRepository,AdresDostawyRepository adresDostawyRepository,
                           ZamowienieRepository zamowienieRepository, SzczegolyZamowieniaRepository szczegolyZamowieniaRepository,
                           DostawaRepository dostawaRepository, DBUserRepository dbUserRepository) {
        this.produktRepository = produktRepository;
        this.adresDostawyRepository = adresDostawyRepository;
        this.zamowienieRepository = zamowienieRepository;
        this.szczegolyZamowieniaRepository = szczegolyZamowieniaRepository;
        this.dostawaRepository = dostawaRepository;
        this.dbUserRepository = dbUserRepository;
    }

    /**
     * Wyświetla podsumowanie zamówienia z produktami znajdującymi się w koszyku.
     *
     * @param session sesja HTTP przechowująca dane koszyka
     * @param model model MVC do przekazania danych do widoku
     * @return widok z podsumowaniem zamówienia ("orders/details")
     */

    @GetMapping("/details")
    public String showOrderSummary(HttpSession session, Model model) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        Map<Produkt, Integer> orderDetails = new HashMap<>();

        if (cart != null) {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                produktRepository.findById(entry.getKey()).ifPresent(produkt ->
                        orderDetails.put(produkt, entry.getValue()));
            }
        }

        float total = 0;
        for (Map.Entry<Produkt, Integer> entry : orderDetails.entrySet()) {
            total += entry.getKey().getCena() * entry.getValue();
        }

        model.addAttribute("totalCost", total);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("adresDostawy", new AdresDostawy());
        return "orders/details";
    }

    /**
     * Obsługuje przesłanie zamówienia: zapisuje adres dostawy, zamówienie, szczegóły zamówienia,
     * aktualizuje stan magazynowy produktów oraz tworzy wpis o dostawie.
     * Po złożeniu zamówienia czyści koszyk w sesji.
     *
     * @param ulica ulica adresu dostawy
     * @param miasto miasto adresu dostawy
     * @param kodPocztowy kod pocztowy adresu dostawy
     * @param kraj kraj adresu dostawy
     * @param sposobDostawy wybrana metoda dostawy
     * @param sposobPlatnosci wybrana metoda płatności
     * @param typKlienta typ klienta (np. indywidualny/firma)
     * @param session sesja HTTP przechowująca dane koszyka
     * @return przekierowanie do strony potwierdzenia zamówienia
     */

    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam String ulica,
            @RequestParam String miasto,
            @RequestParam String kodPocztowy,
            @RequestParam String kraj,
            @RequestParam String sposobDostawy,
            @RequestParam String sposobPlatnosci,
            @RequestParam String typKlienta,
            HttpSession session
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser klient = dbUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego użytkownika"));


        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart"; // brak produktów
        }

        // Oblicz koszt całkowity
        float totalCost = 0;
        Map<Produkt, Integer> produkty = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            produktRepository.findById(entry.getKey()).ifPresent(produkt -> {
                produkty.put(produkt, entry.getValue());
            });
        }
        for (Map.Entry<Produkt, Integer> entry : produkty.entrySet()) {
            totalCost += entry.getKey().getCena() * entry.getValue();
        }

        // Nowy adres dostawy
        AdresDostawy adres = new AdresDostawy();
        adres.setUlica(ulica);
        adres.setMiasto(miasto);
        adres.setKodPocztowy(kodPocztowy);
        adres.setKraj(kraj);
        adres.setKlient(klient);
        adresDostawyRepository.save(adres);

        // Nowe zamówienie
        Zamowienie zamowienie = new Zamowienie();
        zamowienie.setDataZamowienia(LocalDate.now());
        zamowienie.setStatus("w realizacji");
        zamowienie.setKosztCalkowity(totalCost);
        zamowienie.setKlient(klient);
        zamowienie.setAdresDostawy(adres);
        zamowienieRepository.save(zamowienie);

        // Szczegóły zamówienia
        for (Map.Entry<Produkt, Integer> entry : produkty.entrySet()) {
            Produkt produkt = entry.getKey();
            Integer iloscZamowiona = entry.getValue();

            // Tworzenie szczegółów zamówienia
            SzczegolyZamowienia szczegol = new SzczegolyZamowienia();
            szczegol.setZamowienie(zamowienie);
            szczegol.setProdukt(produkt);
            szczegol.setIlosc(iloscZamowiona);
            szczegol.setCenaJednostkowa(produkt.getCena());
            szczegolyZamowieniaRepository.save(szczegol);

            // Aktualizacja stanu magazynowego
            int nowyStanMagazynowy = produkt.getStanMagazynowy() - iloscZamowiona;
            produkt.setStanMagazynowy(nowyStanMagazynowy);

            produktRepository.save(produkt); // zapisujemy zmiany w bazie
        }

        // Dostawa
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date dataWysylki = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date przewidywanaDostawy = calendar.getTime();

        Dostawa dostawa = new Dostawa();
        dostawa.setZamowienie(zamowienie);
        dostawa.setDataWysylki(dataWysylki);
        dostawa.setPrzewidywanaDostawy(przewidywanaDostawy);
        dostawa.setStatus("w trakcie");
        dostawa.setMetodaDostawy(sposobDostawy);
        dostawaRepository.save(dostawa);

        // Wyczyść koszyk po złożeniu
        session.removeAttribute("cart");

        return "redirect:/order/confirmation"; // np. strona z potwierdzeniem zamówienia
    }

    /**
     * Wyświetla stronę z potwierdzeniem złożenia zamówienia.
     *
     * @return widok potwierdzenia zamówienia ("orders/confirmation")
     */

    @GetMapping("/confirmation")
    public String showConfirmationPage() {
        return "orders/confirmation";
    }
}
