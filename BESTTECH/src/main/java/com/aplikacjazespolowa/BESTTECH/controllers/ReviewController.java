package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/opinie")
public class ReviewController {

    @Autowired
    private ZamowienieRepository zamowienieRepository;

    @Autowired
    private RecenzjaRepository recenzjaRepository;

    @Autowired
    private ProduktRepository produktRepository;

    @Autowired
    private DBUserRepository dbUserRepository;

    @GetMapping("")
    public String showOpiniePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        if (email == null) {
            return "redirect:/konto/logowanie";
        }

        DBUser user = dbUserRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/konto/logowanie";
        }

        List<Zamowienie> zamowienia = user.getZamowienia();

        Set<Produkt> kupioneProdukty = new HashSet<>();
        for (Zamowienie zamowienie : zamowienia) {
            if (zamowienie.getSzczegolyZamowienia() != null) {
                for (SzczegolyZamowienia szczegoly : zamowienie.getSzczegolyZamowienia()) {
                    kupioneProdukty.add(szczegoly.getProdukt());
                }
            }
        }

        List<Recenzja> recenzjeUzytkownika = recenzjaRepository.findByKlient(user);

        Set<Produkt> produktyZrecenzowane = recenzjeUzytkownika.stream()
                .map(Recenzja::getProdukt)
                .collect(Collectors.toSet());

        List<Produkt> produktyBezRecenzji = kupioneProdukty.stream()
                .filter(produkt -> !produktyZrecenzowane.contains(produkt))
                .collect(Collectors.toList());

        model.addAttribute("produktyBezRecenzji", produktyBezRecenzji);
        model.addAttribute("recenzjeUzytkownika", recenzjeUzytkownika);

        return "reviews/review";
    }

    @GetMapping("/dodaj_opinie")
    public String showAddReviewPage(@RequestParam("produktId") Integer produktId, Model model) {
        Optional<Produkt> produktOptional = produktRepository.findById(produktId);

        if (produktOptional.isEmpty()) {
            return "redirect:/opinie";
        }

        Produkt produkt = produktOptional.get();
        model.addAttribute("produkt", produkt);
        model.addAttribute("recenzja", new Recenzja());

        return "reviews/add_review";
    }

    @PostMapping("/dodaj_opinie")
    public String saveReview(@RequestParam("produktId") Integer produktId,
                             @ModelAttribute("recenzja") Recenzja recenzja) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        if (email == null) {
            return "redirect:/konto/logowanie";
        }

        DBUser user = dbUserRepository.findByEmail(email).orElse(null);
        Produkt produkt = produktRepository.findById(produktId).orElse(null);

        if (user == null || produkt == null) {
            return "redirect:/opinie";
        }

        recenzja.setKlient(user);
        recenzja.setProdukt(produkt);
        recenzja.setDataDodania(new Date());
        recenzja.setStatus("OCZEKUJACA"); // Domyślnie nowa recenzja jest oczekująca

        recenzjaRepository.save(recenzja);

        return "redirect:/opinie";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/zarzadzaj_opiniami")
    public String showManageReviewsPage(Model model) {
        List<Recenzja> recenzje = recenzjaRepository.findAll();
        model.addAttribute("recenzje", recenzje);
        return "reviews/manage_review";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping("/zaakceptuj")
    public String acceptReview(@RequestParam("id") Integer id) {
        recenzjaRepository.findById(id).ifPresent(recenzja -> {
            recenzja.setStatus("ZAAKCEPTOWANA");
            recenzjaRepository.save(recenzja);
        });
        return "redirect:/opinie/zarzadzaj_opiniami";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping("/odrzuc")
    public String rejectReview(@RequestParam("id") Integer id) {
        recenzjaRepository.findById(id).ifPresent(recenzja -> {
            recenzja.setStatus("ODRZUCONA");
            recenzjaRepository.save(recenzja);
        });
        return "redirect:/opinie/zarzadzaj_opiniami";
    }
}
