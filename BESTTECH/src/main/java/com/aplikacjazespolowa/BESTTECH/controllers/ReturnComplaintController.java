package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @GetMapping
    public String showReturnAndComplaintPage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<DBUser> optionalUser = dbUserRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            DBUser user = optionalUser.get();
            List<Zamowienie> zamowienia = zamowienieRepository.findByKlient_id(user.getId());

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
