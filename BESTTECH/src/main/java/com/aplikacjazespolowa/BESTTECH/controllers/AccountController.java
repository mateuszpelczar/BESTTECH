package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import com.aplikacjazespolowa.BESTTECH.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler obsługujący operacje związane z kontem użytkownika.
 *
 * Odpowiada za rejestrację, logowanie, wyświetlanie danych użytkownika,
 * zarządzanie zamówieniami oraz edycję danych osobowych (imię, nazwisko, telefon, hasło).
 */




@Controller
@RequestMapping("/konto")

public class AccountController {

    // Iniekcja zależności do repozytoriów użytkowników, ról i zamówień oraz kodera haseł

    /**
     * Tworzy nowy kontroler konta z wymaganymi zależnościami.
     *
     * @param userRepo repozytorium użytkowników
     * @param roleRepo repozytorium ról
     * @param passwordEncoder enkoder haseł
     */

    private final DBUserRepository userRepo;
    private final DBRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ZamowienieRepository zamowienieRepository;



    public AccountController(DBUserRepository userRepo, DBRoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Wyświetla szczegóły zalogowanego użytkownika.
     *
     * @param model model do przekazania danych do widoku
     * @return widok z danymi użytkownika
     */


    @GetMapping("/details")
    public String showUserDetails( Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego użytkownika"));


        model.addAttribute("user", user);
        return "konto/userDetails"; // widok: userDetails.html lub userDetails.jsp
    }
    /**
     * Wyświetla formularz rejestracji nowego użytkownika.
     *
     * @return widok formularza rejestracyjnego
     */

    @GetMapping("/rejestracja")
    public String showRegisterForm() {
        return "konto/registerform";
    }
    /**
     * Przetwarza dane rejestracyjne nowego użytkownika.
     *
     * Sprawdza unikalność e-maila, koduje hasło i przypisuje rolę 'CLIENT'.
     *
     * @param email adres e-mail
     * @param password hasło
     * @param imie imię
     * @param nazwisko nazwisko
     * @param telefon numer telefonu
     * @param model model do przekazania błędów/wyników
     * @return przekierowanie lub widok formularza z błędem
     */

    @PostMapping("/rejestracja")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String imie,
                           @RequestParam String nazwisko,
                           @RequestParam String telefon,
                           Model model) {

        DBUser user = new DBUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setImie(imie);
        user.setNazwisko(nazwisko);
        user.setTelefon(telefon);

        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Użytkownik z takim e-mailem już istnieje.");
            return "konto/registerform";
        }


        DBRole clientRole = roleRepo.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Brak roli CLIENT w bazie"));

        user.getRoles().add(clientRole);
        userRepo.save(user);


        return "redirect:/konto/logowanie";
    }


    @GetMapping("/logowanie")
    public String logowanie() {
        return "konto/loginform";
    }
    /**
     * Wyświetla listę zamówień zalogowanego użytkownika.
     *
     * Zamówienia są sortowane według statusu realizacji.
     *
     * @param model model do przekazania danych do widoku
     * @return widok z listą zamówień
     */

    @GetMapping("/zamowienia")
    public String showZamowieniaKlienta(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego użytkownika"));
        List<Zamowienie> zamowienia = zamowienieRepository.findByKlient_id(user.getId());

        List<String> statusOrder = Arrays.asList("w realizacji", "w drodze", "dostarczone");

        List<Zamowienie> posortowaneZamowienia = zamowienia.stream()
                .sorted(
                        Comparator.comparingInt((Zamowienie z) -> statusOrder.indexOf(z.getStatus()))
                                .thenComparing(Zamowienie::getDataZamowienia, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        model.addAttribute("zamowienia", posortowaneZamowienia);


        return "/konto/zamowienia";
    }

    /**
     * Wyświetla formularz zmiany hasła.
     *
     * @return widok formularza zmiany hasła
     */

    @GetMapping("/zmien-haslo")
    public String showChangePasswordForm() {
        return "konto/changepassword";
    }

    /**
     * Przetwarza zmianę hasła zalogowanego użytkownika.
     *
     * Weryfikuje poprawność aktualnego hasła i zgodność nowych haseł.
     *
     * @param oldPassword obecne hasło
     * @param newPassword nowe hasło
     * @param confirmPassword potwierdzenie nowego hasła
     * @param model model do przekazania komunikatów
     * @return widok formularza ze statusem
     */

    @PostMapping("/zmien-haslo")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model
    ) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Nowe hasła nie są takie same.");
            return "konto/changepassword";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Aktualne hasło jest nieprawidłowe.");
            return "konto/changepassword";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        model.addAttribute("success", "Hasło zostało zmienione pomyślnie.");
        return "konto/changepassword";
    }
    /**
     * Wyświetla formularz zmiany imienia użytkownika.
     *
     * @return widok formularza zmiany imienia
     */
    @GetMapping("/zmien-imie")
    public String showChangeNameForm() {
        return "konto/changeName";
    }
    /**
     * Przetwarza zmianę imienia zalogowanego użytkownika.
     *
     * @param newName nowe imię
     * @param model model do przekazania komunikatów
     * @return widok formularza ze statusem
     */

    @PostMapping("/zmien-imie")
    public String changeName(@RequestParam String newName, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika."));

        user.setImie(newName);
        userRepo.save(user);

        model.addAttribute("success", "Imię zostało zmienione pomyślnie.");
        return "konto/changeName";
    }
    /**
     * Wyświetla formularz zmiany nazwiska użytkownika.
     *
     * @return widok formularza zmiany nazwiska
     */

    @GetMapping("/zmien-nazwisko")
    public String showChangeSurnameForm() {
        return "konto/changeSurname";
    }
/**
 * Przetwarza zmianę nazwiska zalogowanego użytkownika.
 */

    @PostMapping("/zmien-nazwisko")
    public String changeSurname(@RequestParam String newSurname, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika."));

        user.setNazwisko(newSurname);
        userRepo.save(user);

        model.addAttribute("success", "Nazwisko zostało zmienione pomyślnie.");
        return "konto/changeSurname";
    }
/**
 * Wyświetla formularz zmiany numeru telefonu użytkownika.
 */

    @GetMapping("/zmien-telefon")
    public String showChangePhoneNumberForm() {
        return "konto/changePhone";
    }
    /**
     * Przetwarza zmianę nazwiska zalogowanego użytkownika.
     *
     * @param //newSurname nowe nazwisko
     * @param model model do przekazania komunikatów
     * @return widok formularza ze statusem
     */
    @PostMapping("/zmien-telefon")
    public String changePhone(@RequestParam String newPhone, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        DBUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika."));

        user.setTelefon(newPhone);
        userRepo.save(user);

        model.addAttribute("success", "Numer telefonu został zmieniony pomyślnie.");
        return "konto/changePhone";
    }







}

