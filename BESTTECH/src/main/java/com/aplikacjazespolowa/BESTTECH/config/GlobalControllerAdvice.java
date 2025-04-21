package com.aplikacjazespolowa.BESTTECH.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("koszykIlosc")
    public int koszykIlosc(HttpSession session) {
        Map<Integer, Integer> koszyk = (Map<Integer, Integer>) session.getAttribute("cart");
        if (koszyk != null) {
            return koszyk.values().stream().mapToInt(Integer::intValue).sum();
        }
        return 0;
    }
}
