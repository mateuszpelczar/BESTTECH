package com.aplikacjazespolowa.BESTTECH.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;
/**
 * Globalny komponent {@code ControllerAdvice} udostępniający atrybut
 * o nazwie {@code koszykIlosc} wszystkim kontrolerom w aplikacji.
 * Dzięki temu atrybutowi możliwe jest łatwe wyświetlanie liczby produktów
 * w koszyku użytkownika na każdej stronie.
 */

@ControllerAdvice
public class GlobalControllerAdvice {
    /**
     * Zwraca łączną liczbę produktów w koszyku użytkownika.
     * Metoda pobiera z sesji atrybut o nazwie {@code cart}, który
     * jest mapą przechowującą identyfikatory produktów i ich ilości.
     * Suma ilości wszystkich produktów jest dostępna w modelu pod
     * nazwą {@code koszykIlosc}.
     *
     * @param session bieżąca sesja HTTP użytkownika
     * @return suma ilości produktów w koszyku; 0, jeśli koszyk jest pusty lub nie istnieje
     */

    @ModelAttribute("koszykIlosc")
    public int koszykIlosc(HttpSession session) {
        Map<Integer, Integer> koszyk = (Map<Integer, Integer>) session.getAttribute("cart");
        if (koszyk != null) {
            return koszyk.values().stream().mapToInt(Integer::intValue).sum();
        }
        return 0;
    }
}
