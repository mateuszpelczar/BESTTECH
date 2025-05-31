package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
/**
 * Kontroler obsługujący zgłoszenia zwrotów i reklamacji produktów przez klientów.
 *
 * Umożliwia:
 * <ul>
 *     <li>Wyświetlanie strony z listą zamówień możliwych do zgłoszenia jako zwrot lub reklamacja</li>
 *     <li>Składanie zgłoszeń zwrotów (do 14 dni od zakupu)</li>
 *     <li>Składanie zgłoszeń reklamacyjnych (do 365 dni od zakupu)</li>
 * </ul>
 */

@Controller
@RequestMapping("/zwroty-reklamacje")
public class ReturnComplaintController {

    @Autowired
    private ZamowienieRepository zamowienieRepository;

    @Autowired
    private ZwrotRepository zwrotRepository;

    @Autowired
    private ReklamacjaRepository reklamacjaRepository;

    @Autowired
    private DBUserRepository dbUserRepository;

    /**
     * Wyświetla stronę zwrotów i reklamacji dla zalogowanego użytkownika.
     *
     * Pobiera listę zamówień przypisaną do zalogowanego klienta oraz oblicza,
     * ile dni minęło od złożenia każdego z nich, by umożliwić określenie czy można je zgłosić.
     *
     * @param model model przekazywany do widoku
     * @return nazwa widoku klienta z listą zamówień ("client/returns")
     */

    @GetMapping
    public String showReturnAndComplaintPage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<DBUser> optionalUser = dbUserRepository.findByEmail(email);

        Sort sortByDateDesc=Sort.by(Sort.Direction.DESC,"dataZamowienia");

        if (optionalUser.isPresent()) {
            DBUser user = optionalUser.get();
            List<Zamowienie> zamowienia = zamowienieRepository.findByKlient_id(user.getId(), sortByDateDesc);

            Map<Integer, Long> dniOdZamowienia = new HashMap<>();
            Date today = new Date();
            for (Zamowienie z : zamowienia) {
                if (z.getDataZamowienia() != null && z.getZamowienieID() != null) {
                    LocalDate dataZamowienia = z.getDataZamowienia();
                    LocalDate dzisiajLocal = LocalDate.now();

                    // Konwersja LocalDate do Instant na początku dnia
                    long dni = ChronoUnit.DAYS.between(dataZamowienia.atStartOfDay(), dzisiajLocal.atStartOfDay());
                    dniOdZamowienia.put(z.getZamowienieID(), dni);
                }
            }


            model.addAttribute("zamowienia", zamowienia);
            model.addAttribute("dniMap", dniOdZamowienia);
        }

        return "client/returns";
    }

    /**
     * Obsługuje zgłoszenie zwrotu produktu.
     *
     * Zgłoszenie jest możliwe tylko wtedy, gdy od daty zamówienia nie minęło więcej niż 14 dni.
     *
     * @param zamowienieId ID zamówienia, którego dotyczy zwrot
     * @param powod        powód zgłoszenia zwrotu
     * @return przekierowanie do strony zwrotów i reklamacji
     */

    @PostMapping("/submit-return")
    public String submitReturn(@RequestParam("zamowienieId") Integer zamowienieId,
                               @RequestParam("powod") String powod) {
        Optional<Zamowienie> optionalZamowienie = zamowienieRepository.findById(zamowienieId);
        if (optionalZamowienie.isPresent()) {
            Zamowienie zamowienie = optionalZamowienie.get();
            long dni = ChronoUnit.DAYS.between(
                    zamowienie.getDataZamowienia().atStartOfDay(),
                    LocalDate.now().atStartOfDay()
            );
            if (dni <= 14) {
                Zwrot zwrot = new Zwrot();
                zwrot.setZamowienie(zamowienie);
                zwrot.setDataZwrotu(new Date());
                zwrot.setStatus("OCZEKUJACY");
                zwrot.setPowod(powod);
                zwrotRepository.save(zwrot);
            }
        }
        return "redirect:/zwroty-reklamacje";
    }

    /**
     * Obsługuje zgłoszenie reklamacji produktu.
     *
     * Zgłoszenie reklamacji jest możliwe tylko wtedy, gdy od daty zamówienia nie minęło więcej niż 365 dni.
     *
     * @param zamowienieId ID zamówienia, którego dotyczy reklamacja
     * @param opis         opis problemu podany przez klienta
     * @return przekierowanie do strony zwrotów i reklamacji
     */

    @PostMapping("/submit-complaint")
    public String submitComplaint(@RequestParam("zamowienieId") Integer zamowienieId,
                                  @RequestParam("opis") String opis) {
        Optional<Zamowienie> optionalZamowienie = zamowienieRepository.findById(zamowienieId);
        if (optionalZamowienie.isPresent()) {
            Zamowienie zamowienie = optionalZamowienie.get();
            long dni = ChronoUnit.DAYS.between(
                    zamowienie.getDataZamowienia().atStartOfDay(),
                    LocalDate.now().atStartOfDay()
            );


            if (dni <= 365) {
                Reklamacja reklamacja = new Reklamacja();
                reklamacja.setZamowienie(zamowienie);
                reklamacja.setDataZgloszenia(new Date());
                reklamacja.setStatus("OCZEKUJACA");
                reklamacja.setOpis(opis);
                reklamacjaRepository.save(reklamacja);
            }
        }
        return "redirect:/zwroty-reklamacje";
    }
}
