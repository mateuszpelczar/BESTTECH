package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Kontroler odpowiadający za obsługę recenzji produktów w aplikacji sklepu internetowego.
 *
 * Obsługuje działania użytkowników (klientów) oraz pracowników i administratorów związane z opiniami o produktach.
 * Umożliwia m.in. dodawanie, przeglądanie oraz moderowanie recenzji.
 */
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

    /**
     * Wyświetla stronę recenzji użytkownika.
     *
     * Pokazuje produkty, które użytkownik kupił i może ocenić, a także recenzje,
     * które już napisał.
     *
     * @param model model przekazywany do widoku
     * @return widok strony opinii użytkownika
     */

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
        Sort sortByDateDesc=Sort.by(Sort.Direction.DESC,"dataDodania");
        List<Recenzja> recenzjeUzytkownika = recenzjaRepository.findByKlient(user,sortByDateDesc);

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

    /**
     * Wyświetla formularz dodawania nowej recenzji dla wybranego produktu.
     *
     * @param produktId ID produktu, dla którego dodawana jest recenzja
     * @param model     model przekazywany do widoku
     * @return widok formularza dodawania opinii
     */

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

    /**
     * Zapisuje nową recenzję dodaną przez użytkownika.
     *
     * Nowa opinia domyślnie otrzymuje status „OCZEKUJĄCA”.
     *
     * @param produktId ID produktu, którego dotyczy recenzja
     * @param recenzja  obiekt recenzji wypełniony przez użytkownika
     * @return przekierowanie do strony opinii
     */

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

    /**
     * Wyświetla panel zarządzania opiniami.
     *
     * Dostępny tylko dla użytkowników z rolą ADMIN lub EMPLOYEE.
     * Pokazuje wszystkie recenzje w systemie.
     *
     * @param model model przekazywany do widoku
     * @return widok panelu zarządzania recenzjami
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/zarzadzaj_opiniami")
    public String showManageReviewsPage(Model model) {
        Sort sortByDateDesc=Sort.by(Sort.Direction.DESC,"dataDodania");
        List<Recenzja> recenzje = recenzjaRepository.findAll(sortByDateDesc);
        model.addAttribute("recenzje", recenzje);
        return "reviews/manage_review";
    }

    /**
     * Zmienia status wybranej recenzji na „ZAAKCEPTOWANA”.
     *
     * Dostępne tylko dla ADMINA lub PRACOWNIKA.
     *
     * @param id ID recenzji do zatwierdzenia
     * @return przekierowanie do panelu zarządzania recenzjami
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PostMapping("/zaakceptuj")
    public String acceptReview(@RequestParam("id") Integer id) {
        recenzjaRepository.findById(id).ifPresent(recenzja -> {
            recenzja.setStatus("ZAAKCEPTOWANA");
            recenzjaRepository.save(recenzja);
        });
        return "redirect:/opinie/zarzadzaj_opiniami";
    }

    /**
     * Zmienia status wybranej recenzji na „ODRZUCONA”.
     *
     * Dostępne tylko dla ADMINA lub PRACOWNIKA.
     *
     * @param id ID recenzji do odrzucenia
     * @return przekierowanie do panelu zarządzania recenzjami
     */

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