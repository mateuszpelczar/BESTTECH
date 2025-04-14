package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.AdresDostawy;
import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final ProduktRepository produktRepository;

    public OrderController(ProduktRepository produktRepository) {
        this.produktRepository = produktRepository;
    }

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

}
