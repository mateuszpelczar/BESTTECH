package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/produkty")
public class ProductsController {


    @GetMapping
    public String mainPage() {
        return "produkty/index";
    }

    @GetMapping("/zarzadzaj")
    public String zarzadzajProduktami() {
        return "produkty/zarzadzajProduktami";
    }


}
