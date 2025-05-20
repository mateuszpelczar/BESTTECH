package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Kontroler obsługujący operacje związane z koszykiem zakupowym.
 *
 * Umożliwia dodawanie produktów do koszyka, usuwanie ich, aktualizowanie ilości,
 * przeglądanie zawartości koszyka oraz jego czyszczenie.
 */




@Controller
@RequestMapping("/koszyk")
public class CartController {

    private final ProduktRepository produktRepository;

    /**
     * Konstruktor wstrzykujący repozytorium produktów.
     *
     * @param produktRepository repozytorium do operacji na produktach
     */

    public CartController(ProduktRepository produktRepository) {
        this.produktRepository = produktRepository;
    }

    /**
     * Wyświetla zawartość koszyka.
     *
     * @param session sesja HTTP, z której pobierane są dane koszyka
     * @param model   model MVC do przekazania danych do widoku
     * @return widok koszyka (orders/cart)
     */

    @GetMapping
    public String showCart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        Map<Produkt, Integer> produktyWKoszyku = new HashMap<>();

        if (cart != null) {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                produktRepository.findById(entry.getKey()).ifPresent(produkt ->
                        produktyWKoszyku.put(produkt, entry.getValue()));
            }
        }

        model.addAttribute("produkty", produktyWKoszyku);
        return "orders/cart";
    }

    /**
     * Dodaje produkt do koszyka na podstawie jego ID.
     *
     * @param produktId ID produktu do dodania
     * @param session   sesja HTTP, w której przechowywany jest koszyk
     * @return przekierowanie do widoku koszyka
     */

    @PostMapping("/dodaj/{id}")
    public String addToCart(@PathVariable("id") Integer produktId, HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(produktId, cart.getOrDefault(produktId, 0) + 1);
        session.setAttribute("cart", cart);
        return "redirect:/koszyk";
    }
    /**
     * Usuwa produkt z koszyka na podstawie jego ID.
     *
     * @param produktId ID produktu do usunięcia
     * @param session   sesja HTTP, w której przechowywany jest koszyk
     * @return przekierowanie do widoku koszyka
     */

    @PostMapping("/usun/{id}")
    public String removeFromCart(@PathVariable("id") Integer produktId, HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null) {
            cart.remove(produktId);
            session.setAttribute("cart", cart);
        }

        return "redirect:/koszyk";
    }

    /**
     * Aktualizuje ilości produktów w koszyku.
     *
     * @param ids     lista ID produktów
     * @param ilosci  lista odpowiadających ilości
     * @param session sesja HTTP, w której przechowywany jest koszyk
     * @return przekierowanie do widoku koszyka
     */

    @PostMapping("/zmien-ilosc")
    public String updateQuantities(
            @RequestParam("id") List<Integer> ids,
            @RequestParam("ilosc") List<Integer> ilosci,
            HttpSession session
    ) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null) {
            for (int i = 0; i < ids.size(); i++) {
                Integer produktId = ids.get(i);
                Integer ilosc = ilosci.get(i);

                Produkt produkt = produktRepository.findById(produktId).orElse(null);

                if (produkt != null) {
                    int dostepnaIlosc = produkt.getStanMagazynowy();

                    if (ilosc > dostepnaIlosc) {
                        ilosc = dostepnaIlosc;
                    }

                    if (ilosc > 0) {
                        cart.put(produktId, ilosc);
                    } else {
                        cart.remove(produktId);
                    }
                }
            }
            session.setAttribute("cart", cart);
        }

        return "redirect:/koszyk";
    }
    /**
     * Czyści cały koszyk zakupowy.
     *
     * @param session sesja HTTP, w której przechowywany jest koszyk
     * @return przekierowanie do widoku koszyka
     */

    @PostMapping("/wyczysc")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/koszyk";
    }
}
