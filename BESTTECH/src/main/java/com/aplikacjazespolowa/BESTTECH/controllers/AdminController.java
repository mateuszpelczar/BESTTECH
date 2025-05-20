package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Kontroler administratora odpowiedzialny za zarządzanie użytkownikami, pracownikami,
 * logami systemowymi oraz reklamacjami i zwrotami.
 *
 * Umożliwia m.in.:
 * - przeglądanie i edycję ról użytkowników,
 * - usuwanie pracowników,
 * - przeglądanie logów systemowych,
 * - obsługę zwrotów i reklamacji.
 */


@Controller
@RequestMapping("/admin")



public class AdminController {

    @Autowired
    private DBUserRepository userRepository;
    @Autowired
    private DBRoleRepository roleRepository;
    @Autowired
    private LogsRepository logsRepository;
    @Autowired
    private HttpServletRequest request; //aby pobrac aktualnego uzytkownika;
    @Autowired
    private ZwrotRepository zwrotRepository;

    @Autowired
    private ReklamacjaRepository reklamacjaRepository;

    /**
     * Strona główna panelu administratora.
     *
     * @return widok panelu administratora
     */

    @GetMapping
    public String adminPanel() {
        return "admin/adminPanel";
    }
    /**
     * Wyświetla listę wszystkich użytkowników do zarządzania.
     *
     * @param model model przekazujący listę użytkowników
     * @return widok z listą użytkowników
     */
    @GetMapping("/manageusers")
    public String manageUsers(Model model) {

        List<DBUser> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/manage_users";
    }
    /**
     * Wyświetla formularz do zmiany ról wybranego użytkownika.
     *
     * @param userId ID użytkownika
     * @param model model przekazujący użytkownika i listę ról
     * @return widok formularza zmiany ról
     */
    @GetMapping("/manageusers/changerole")
    public String changeUserRole(@RequestParam Integer userId, Model model) {

        DBUser user = userRepository.findById(userId).orElseThrow();
        List<DBRole> allRoles = roleRepository.findAll();


        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/change_role";
    }
    /**
     * Zapisuje zmienione role użytkownika.
     *
     * @param userId ID użytkownika
     * @param roleIds lista wybranych ról (opcjonalna)
     * @return przekierowanie do zarządzania użytkownikami
     */
    @PostMapping("/manageusers/changerole/save")
    public String saveUserRoles(@RequestParam Integer userId,
                                @RequestParam(required = false) List<Integer> roleIds) {

        DBUser user = userRepository.findById(userId).orElseThrow();

        Set<DBRole> newRoles = new HashSet<>();
        if (roleIds != null) {
            newRoles.addAll(roleRepository.findAllById(roleIds));
        }

        user.setRoles(newRoles);
        userRepository.save(user);

        return "redirect:/admin/manageusers";
    }
    /**
     * Wyświetla listę pracowników do zarządzania.
     *
     * @param model model przekazujący listę pracowników
     * @return widok z pracownikami
     */

    @GetMapping("/manageemployess")
    public String manageEmployees(Model model){
        List<DBUser> employees = userRepository.findByRoles_Name("EMPLOYEE");
        model.addAttribute("employees",employees);
        return "admin/manage_employees";
    }
    /**
     * Wyświetla formularz usuwania pracowników.
     *
     * @param model model przekazujący listę pracowników
     * @return widok do usuwania pracowników
     */

    @GetMapping("/deleteemployee")
    public String deleteEmployee(Model model){
        List<DBUser> employees = userRepository.findByRoles_Name("EMPLOYEE");
        model.addAttribute("employees",employees);
        return "admin/delete_employee";
    }
    /**
     * Usuwa wskazanego pracownika i zapisuje informację w logach.
     *
     * @param userId ID pracownika do usunięcia
     * @param redirectAttributes do przekazania wiadomości do widoku
     * @param request obiekt żądania do pobrania zalogowanego użytkownika
     * @return przekierowanie do widoku usuwania pracowników
     */

    @PostMapping("/deleteemployee")
    public String deletedEmployee(@RequestParam Integer userId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Optional<DBUser> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            DBUser user = userOptional.get();

            //dodanie loga przez usunieciem
            String currentUser = request.getUserPrincipal().getName();  //aktualnie zalogowany user
            String deletedEmail = user.getEmail();  //email usuwanego
            logsRepository.save(new LogsSystem("Usunieto pracownika: "+deletedEmail, currentUser, "WARN"));

            userRepository.deleteById(userId);
            redirectAttributes.addFlashAttribute("message", "Użytkownik został pomyślnie usunięty.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Użytkownik o podanym ID nie istnieje.");
        }




        return "redirect:/admin/deleteemployee";
    }
    /**
     * Wyświetla logi systemowe.
     *
     * @param model model przekazujący logi
     * @return widok logów systemowych
     */
    @GetMapping("/admin/logs")
    public String pokazLogi(Model model){
        List<LogsSystem>logi = logsRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        model.addAttribute("logi", logi);
        return "admin/logs";
    }


    @Autowired
    private ZamowienieRepository zamowienieRepository;
    /**
     * Wyświetla zamówienia do obsługi zwrotów i reklamacji.
     *
     * @param model model przekazujący zamówienia i dni od zakupu
     * @return widok zarządzania zwrotami i reklamacjami
     */
    @GetMapping("/zwroty-reklamacje-administrator")
    public String zarzadzanieZwrotamiReklamacji(Model model) {
        List<Zamowienie> zamowienia = zamowienieRepository.findAll();
        model.addAttribute("zamowienia", zamowienia);

        Map<Integer, Integer> dniOdZamowieniaMap = new HashMap<>();
        Date dzisiaj = new Date();
        for (Zamowienie z : zamowienia) {
            if (z.getDataZamowienia() != null && z.getZamowienieID() != null) {
                LocalDate dataZamowienia = z.getDataZamowienia();
                LocalDate dzisiajLocal = LocalDate.now();

                long dni = ChronoUnit.DAYS.between(dataZamowienia, dzisiajLocal);
                dniOdZamowieniaMap.put(z.getZamowienieID(), (int) dni);
            }
        }



        model.addAttribute("dniOdZamowieniaMap", dniOdZamowieniaMap);
        return "admin/returnsMOD";
    }

    /**
     * Akceptuje wskazany zwrot.
     *
     * @param id ID zwrotu
     * @return przekierowanie do widoku obsługi zwrotów i reklamacji
     */
    // Akceptacja zwrotu
    @PostMapping("/zwroty/akceptuj")
    public String akceptujZwrot(@RequestParam("zwrotID") Integer id) {
        Zwrot zwrot = zwrotRepository.findById(id).orElse(null);
        if (zwrot != null) {
            zwrot.setStatus("zaakceptowany");
            zwrotRepository.save(zwrot);
        }
        return "redirect:/admin/zwroty-reklamacje-administrator";
    }


    /**
     * Odrzuca wskazaną reklamację.
     *
     * @param id ID reklamacji
     * @return przekierowanie do widoku obsługi zwrotów i reklamacji
     */
    // Odrzucenie reklamacji
    @PostMapping("/reklamacje/odrzuc")
    public String odrzucReklamacje(@RequestParam("reklamacjaID") Integer id) {
        Reklamacja reklamacja = reklamacjaRepository.findById(id).orElse(null);
        if (reklamacja != null) {
            reklamacja.setStatus("odrzucona");
            reklamacjaRepository.save(reklamacja);
        }
        return "redirect:/admin/zwroty-reklamacje-administrator";
    }
    /**
     * Dodaje komentarz administratora do zwrotu.
     *
     * @param id ID zwrotu
     * @param komentarz treść komentarza
     * @return przekierowanie do widoku obsługi zwrotów i reklamacji
     */
    @PostMapping("/zwroty/dodaj-komentarz")
    public String dodajKomentarzDoZwrotu(@RequestParam("zwrotID") Integer id,
                                         @RequestParam("komentarz") String komentarz) {
        Zwrot zwrot = zwrotRepository.findById(id).orElse(null);
        if (zwrot != null) {
            zwrot.setKomentarzAdmina(komentarz);
            zwrotRepository.save(zwrot);
        }
        return "redirect:/admin/zwroty-reklamacje-administrator";
    }
    /**
     * Dodaje komentarz administratora do reklamacji.
     *
     * @param id ID reklamacji
     * @param komentarz treść komentarza
     * @return przekierowanie do widoku obsługi zwrotów i reklamacji
     */
    @PostMapping("/reklamacje/dodaj-komentarz")
    public String dodajKomentarzDoReklamacji(@RequestParam("reklamacjaID") Integer id,
                                             @RequestParam("komentarz") String komentarz) {
        Reklamacja reklamacja = reklamacjaRepository.findById(id).orElse(null);
        if (reklamacja != null) {
            reklamacja.setKomentarzAdmina(komentarz);
            reklamacjaRepository.save(reklamacja);
        }
        return "redirect:/admin/zwroty-reklamacje-administrator";
    }




}
