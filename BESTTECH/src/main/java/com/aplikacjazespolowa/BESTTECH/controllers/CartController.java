package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/koszyk")
public class CartController {

    private final ProduktRepository produktRepository;

    public CartController(ProduktRepository produktRepository) {
        this.produktRepository = produktRepository;
    }

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

    @PostMapping("/usun/{id}")
    public String removeFromCart(@PathVariable("id") Integer produktId, HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null) {
            cart.remove(produktId);
            session.setAttribute("cart", cart);
        }

        return "redirect:/koszyk";
    }

    @PostMapping("/zmien-ilosc")
    public String updateQuantity(@RequestParam("id") Integer produktId,
                                 @RequestParam("ilosc") Integer ilosc,
                                 HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null && ilosc > 0) {
            cart.put(produktId, ilosc);
            session.setAttribute("cart", cart);
        }

        return "redirect:/koszyk";
    }

    @PostMapping("/wyczysc")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/koszyk";
    }
}
