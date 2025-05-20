package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Kontroler testowy używany do weryfikacji działania widoku oraz routingu.
 *
 * Może służyć jako tymczasowa strona do sprawdzania poprawności działania frameworka Spring, szablonów HTML,
 * integracji z bazą danych lub zabezpieczeń.
 */
@Controller
public class TestController {
    /**
     * Wyświetla prosty widok testowy.
     *
     * Dostępny pod adresem `/test`. Służy do celów testowych.
     *
     * @return nazwa widoku testowego (test.html)
     */
    @GetMapping("/test")
    public String testView() {
        return "test";
    }
}
